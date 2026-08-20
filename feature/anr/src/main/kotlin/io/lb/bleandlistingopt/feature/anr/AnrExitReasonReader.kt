package io.lb.bleandlistingopt.feature.anr

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build

/**
 * `ApplicationExitInfo` with `REASON_ANR` is how a triggered ANR surfaces
 * locally: the system kills the process, and only on the *next* launch can
 * this be read back -- it's a record of the previous process's death, not
 * something the current process can query about itself while it's alive.
 * This is also, separately (and with the usual reporting delay), what
 * Crashlytics reads to populate its own ANR reports in the console -- same
 * underlying mechanism, two different readers of it.
 */
fun readLastAnrReason(context: Context): String? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return "Requires API 30+ (getHistoricalProcessExitReasons)"

    val activityManager = context.getSystemService(ActivityManager::class.java)
    val reasons = activityManager.getHistoricalProcessExitReasons(context.packageName, 0, 10)
    val anr = reasons.firstOrNull { it.reason == ApplicationExitInfo.REASON_ANR } ?: return null
    return "ANR at ${anr.timestamp}: ${anr.description}"
}
