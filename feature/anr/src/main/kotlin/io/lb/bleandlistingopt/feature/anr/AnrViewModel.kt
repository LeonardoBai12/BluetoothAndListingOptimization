package io.lb.bleandlistingopt.feature.anr

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.lb.bleandlistingopt.core.common.DefaultDispatcherProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.concurrent.thread

/**
 * Every trigger/fix pair below runs the *same* work, the only difference is
 * which thread it runs on. Each pair is time-bounded to ~6s so the input
 * dispatch ANR timeout (~5s) is reliably crossed regardless of device
 * speed. AndroidViewModel (not plain ViewModel) is used only because the
 * disk-write demo needs a `cacheDir` -- Application context is safe to hold
 * for a ViewModel's lifetime, unlike an Activity context.
 */
class AnrViewModel(application: Application) : AndroidViewModel(application) {
    private val dispatchers = DefaultDispatcherProvider()

    private val _state = MutableStateFlow(AnrState(lastAnrReason = readLastAnrReason(application)))
    val state: StateFlow<AnrState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AnrEffect>()
    val effects: SharedFlow<AnrEffect> = _effects.asSharedFlow()

    // Two locks acquired in opposite orders by the anti-pattern (see
    // triggerDeadlock/fixDeadlock) -- kept as fields so both the trigger and
    // its fix contend over the exact same locks.
    private val lockA = Any()
    private val lockB = Any()

    fun onEvent(event: AnrEvent) {
        when (event) {
            AnrEvent.OnTriggerSleep -> triggerSleep()
            AnrEvent.OnFixSleep -> fixSleep()
            AnrEvent.OnTriggerCpuLoop -> triggerCpuLoop()
            AnrEvent.OnFixCpuLoop -> fixCpuLoop()
            AnrEvent.OnTriggerDiskRead -> triggerDiskRead()
            AnrEvent.OnFixDiskRead -> fixDiskRead()
            AnrEvent.OnTriggerDeadlock -> triggerDeadlock()
            AnrEvent.OnFixDeadlock -> fixDeadlock()
        }
    }

    // --- 1. Thread.sleep() on main vs. off main -----------------------

    // ANTI-PATTERN: called directly from the click handler, so this runs ON
    // the main thread -- there's no dispatcher involved, it just blocks
    // whatever thread called onEvent(). Since Compose dispatches click
    // handling as part of input-event processing, the input dispatcher's
    // ~5s timeout starts counting from the moment this event was posted,
    // and this alone blows well past it.
    private fun triggerSleep() {
        _state.update { it.copy(status = "Sleeping on main thread for 6s...") }
        Thread.sleep(6_000)
        _state.update { it.copy(status = "Woke up after 6s (main thread was frozen)") }
    }

    // FIX: same 6s wait, launched onto Dispatchers.Default instead of run
    // inline. onEvent() returns immediately; delay() suspends the coroutine
    // without blocking any thread, so the UI stays responsive the whole time.
    private fun fixSleep() {
        _state.update { it.copy(status = "Waiting 6s off the main thread...") }
        viewModelScope.launch(dispatchers.default) {
            delay(6_000)
            _state.update { it.copy(status = "Done waiting 6s (main thread stayed responsive)") }
        }
    }

    // --- 2. Heavy CPU work on main vs. off main ------------------------

    // ANTI-PATTERN: real CPU work (trial-division primality checks) run
    // synchronously in the click handler -- what "the app is doing
    // something expensive on main" actually looks like, as opposed to just
    // sleeping. Time-bounded rather than a fixed prime limit, since a fixed
    // limit would run in wildly different real time on different CPUs.
    private fun triggerCpuLoop() {
        _state.update { it.copy(status = "Computing primes on main thread...") }
        val count = countPrimesFor(6_000)
        _state.update { it.copy(status = "Found $count primes (main thread was frozen)") }
    }

    private fun fixCpuLoop() {
        _state.update { it.copy(status = "Computing primes off the main thread...") }
        viewModelScope.launch(dispatchers.default) {
            val count = countPrimesFor(6_000)
            _state.update { it.copy(status = "Found $count primes (main thread stayed responsive)") }
        }
    }

    private fun countPrimesFor(durationMillis: Long): Int {
        val start = System.currentTimeMillis()
        var candidate = 2
        var count = 0
        while (System.currentTimeMillis() - start < durationMillis) {
            if (isPrime(candidate)) count++
            candidate++
        }
        return count
    }

    private fun isPrime(n: Int): Boolean {
        if (n < 2) return false
        var divisor = 2
        while (divisor * divisor <= n) {
            if (n % divisor == 0) return false
            divisor++
        }
        return true
    }

    // --- 3. Blocking disk I/O on main vs. Dispatchers.IO ---------------

    // ANTI-PATTERN: synchronous file writes with an fsync per chunk -- each
    // fsync blocks until the OS confirms the write actually reached
    // storage, not just a page cache, which is what makes this genuinely
    // slow rather than an instant buffered write. This is exactly the kind
    // of call StrictMode's disk-write policy (see StrictModeSetup, enabled
    // in the app) flags on its own, independent of whether it happens to
    // run long enough to also cause an ANR.
    private fun triggerDiskRead() {
        _state.update { it.copy(status = "Writing to disk on main thread...") }
        blockingDiskWork(6_000)
        _state.update { it.copy(status = "Done writing (main thread was frozen)") }
    }

    private fun fixDiskRead() {
        _state.update { it.copy(status = "Writing to disk off the main thread...") }
        viewModelScope.launch(dispatchers.io) {
            blockingDiskWork(6_000)
            _state.update { it.copy(status = "Done writing (main thread stayed responsive)") }
        }
    }

    private fun blockingDiskWork(durationMillis: Long) {
        val file = File(getApplication<Application>().cacheDir, "anr_lab_scratch.bin")
        val chunk = ByteArray(CHUNK_SIZE_BYTES)
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < durationMillis) {
            FileOutputStream(file).use { stream ->
                stream.write(chunk)
                stream.fd.sync()
            }
        }
        file.delete()
    }

    // --- 4. Two-lock deadlock vs. consistent lock ordering -------------

    // ANTI-PATTERN: a classic lock-ordering deadlock, with the main thread
    // as one of the two parties -- that's what turns "two threads
    // deadlocked" into an ANR instead of just two background threads
    // quietly hanging forever where nobody notices. This thread (main,
    // since this runs directly in the click handler) takes lockA then
    // tries lockB; the spawned thread takes lockB then tries lockA. The
    // staggered 200ms sleep after each thread's first lock is what makes
    // this reliably reproduce: it gives the other thread time to grab its
    // own first lock before either tries for the second one. There is no
    // timeout on `synchronized` -- once both threads are waiting on each
    // other's lock, they wait forever. The only way out is the ANR
    // dialog's "close app".
    private fun triggerDeadlock() {
        _state.update { it.copy(status = "Deadlocking...") }
        thread {
            synchronized(lockB) {
                Thread.sleep(LOCK_STAGGER_MILLIS)
                synchronized(lockA) { /* never reached */ }
            }
        }
        Thread.sleep(LOCK_STAGGER_MILLIS)
        synchronized(lockA) {
            synchronized(lockB) { /* never reached on main thread either */ }
        }
    }

    // FIX: consistent lock ordering. Every thread that needs both locks
    // acquires lockA first, then lockB -- always in that order. With no
    // circular wait possible, the two threads can, at worst, briefly wait
    // for each other's *turn*, never for each other's *lock*.
    private fun fixDeadlock() {
        _state.update { it.copy(status = "Locking in consistent order...") }
        thread {
            synchronized(lockA) {
                Thread.sleep(LOCK_STAGGER_MILLIS)
                synchronized(lockB) { /* fine -- same order as below */ }
            }
        }
        viewModelScope.launch(dispatchers.default) {
            delay(LOCK_STAGGER_MILLIS)
            synchronized(lockA) {
                synchronized(lockB) { /* fine -- same order as above */ }
            }
            _state.update { it.copy(status = "No deadlock (consistent lock order)") }
        }
    }

    private companion object {
        const val CHUNK_SIZE_BYTES = 4096
        const val LOCK_STAGGER_MILLIS = 200L
    }
}
