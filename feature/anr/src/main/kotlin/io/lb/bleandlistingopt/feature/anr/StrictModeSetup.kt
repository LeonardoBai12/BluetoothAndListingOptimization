package io.lb.bleandlistingopt.feature.anr

import android.os.StrictMode

/**
 * Flags disk/network calls made from the main thread as they happen
 * (`penaltyLog`) and interrupts with a dialog (`penaltyDialog`) -- this is
 * what would catch [AnrLabActivity]'s "disk read on main" anti-pattern even
 * without deliberately watching for it. It does NOT catch `Thread.sleep()`,
 * a CPU-bound loop, or a lock wait: StrictMode only instruments specific
 * I/O-shaped calls, not "the thread was blocked for a while" in general --
 * that's the gap the ANR timeout itself exists to cover.
 */
fun enableStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .penaltyDialog()
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
