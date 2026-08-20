package io.lb.bleandlistingopt.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies

@Component
interface AppComponent : BluetoothDependencies {
    override fun context(): Context

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}
