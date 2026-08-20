package io.lb.bleandlistingopt

import android.app.Application
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import io.lb.bleandlistingopt.di.AppComponent
import io.lb.bleandlistingopt.di.DaggerAppComponent
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies

// StrictMode is deliberately NOT enabled here. Turning penaltyDialog on this
// early catches legitimate framework disk I/O during cold start (Firebase
// reading its config, Dagger's reflection-based setup, resource loading) as
// false-positive violations, which just interrupts every launch with an
// unrelated dialog. It's enabled instead in AnrLabActivity, so it's scoped
// to exactly the screen whose deliberate disk-write anti-pattern it's meant
// to catch -- see StrictModeSetup.kt for what it actually detects.
class BleLabApplication : Application(), BluetoothDependencies {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.factory().create(this)
        // Firebase.initialize() runs implicitly via the ContentProvider
        // FirebaseInitProvider; touching Crashlytics here just confirms
        // it's live, useful when checking the ANR Lab's exit-reason flow.
        Firebase.crashlytics.setCustomKey("app_started", true)
    }

    override fun context(): Context = this
}
