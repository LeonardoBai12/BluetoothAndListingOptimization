# ANR Lab

Each row in the lab screen triggers a distinct main-thread stall and shows
its fix. Triggering one freezes the UI for ~6s (or, for the deadlock pair,
forever, until the system's ANR dialog appears) -- that's the point.

## Reading the trace after a triggered ANR

1. `adb shell am start -n io.lb.bleandlistingopt/.MainActivity`, navigate to
   the ANR Lab, tap a "Trigger ANR" button, and wait for the system "app
   isn't responding" dialog (or force it: `adb shell am hang` is a coarser
   alternative for the whole system, prefer the lab's own buttons).
2. Pull the trace: `adb pull /data/anr/traces.txt` (or, on devices that
   don't expose `/data/anr` without root, `adb bugreport bugreport.zip` and
   extract `FS/data/anr/traces.txt` from it). `adb shell dumpsys activity
   processes | grep -A 30 io.lb.bleandlistingopt` also shows a live snapshot
   while the ANR dialog is still up.
3. In the trace, find the thread named `"main"`. Its stack shows exactly
   what the main thread was doing when the watchdog fired -- for the
   `Thread.sleep()` trigger, that's a frame inside `Thread.sleep`; for the
   deadlock trigger, it's a frame inside `Object.wait`/monitor contention,
   with a second thread's stack elsewhere in the same trace holding the
   lock this one is blocked on.

## The Crashlytics flow

Crashlytics reads the same underlying signal as this lab's own reader
(`ActivityManager.getHistoricalProcessExitReasons`, `REASON_ANR`) -- via
`ApplicationExitInfo`, not by hooking the crash live. Sequence to see both:

1. Trigger an ANR in the lab and let the process actually get killed (close
   it from the ANR dialog, or force-stop: `adb shell am force-stop
   io.lb.bleandlistingopt`).
2. Relaunch the app. `AnrViewModel` calls `readLastAnrReason()` on init,
   which shows the local reading immediately, in-app, with no delay.
3. Separately, Crashlytics uploads its own ANR report on a later app
   launch/foreground event and it appears in the Firebase console under
   Crashlytics > ANRs after its usual reporting delay (minutes, sometimes
   longer) -- it is not instantaneous like the local reader.
