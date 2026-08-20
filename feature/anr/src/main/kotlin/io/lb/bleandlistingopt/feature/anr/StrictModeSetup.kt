package io.lb.bleandlistingopt.feature.anr

import android.os.StrictMode

/**
 * Flags disk/network calls made from the main thread as they happen, in
 * Logcat -- this is what would catch [AnrLabActivity]'s "disk read on main"
 * anti-pattern even without deliberately watching for it. It does NOT catch
 * `Thread.sleep()`, a CPU-bound loop, or a lock wait: StrictMode only
 * instruments specific I/O-shaped calls, not "the thread was blocked for a
 * while" in general -- that's the gap the ANR timeout itself exists to
 * cover.
 *
 * `penaltyDialog()` is deliberately NOT used, even though it's the more
 * visible classic pairing with `penaltyLog()`. On-device testing hit a real,
 * reproducible failure mode with it on: the dialog it shows is itself modal
 * and briefly blocks input dispatch, and that alone was enough to trip the
 * *system's* separate ANR watchdog on this Activity ("Input dispatching
 * timed out... waited 20004ms for FocusEvent") -- a StrictMode violation
 * cascading into a real, confusing ANR that had nothing to do with whichever
 * anti-pattern button was (or wasn't) pressed. `penaltyLog()` alone carries
 * no such risk: it only ever writes to Logcat, never blocks anything.
 */
fun enableStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build(),
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build(),
    )
}

/**
 * `StrictMode.setThreadPolicy`/`setVmPolicy` are process-global, not scoped
 * to whichever Activity called them -- calling [enableStrictMode] in
 * `onCreate` and never undoing it leaves the policy active for the rest of
 * the process's life, including every other screen, which is exactly what
 * made it fire on totally unrelated disk/network calls elsewhere in the app
 * after just visiting this one. Call this when leaving the screen (e.g.
 * `onPause`) to actually scope it to "while this screen is in the
 * foreground", not "from now on, forever, in this process".
 */
fun disableStrictMode() {
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX)
    StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX)
}
