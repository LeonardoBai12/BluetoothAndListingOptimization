package io.lb.bleandlistingopt.feature.bluetooth.presentation.di

import dagger.Component
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDataModule
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies

/**
 * A component *dependency* (not a subcomponent): this graph only sees what
 * [BluetoothDependencies] exposes from the app-level graph, not that
 * graph's entire binding set. `:feature:bluetooth:presentation` never
 * references `:app` directly -- the app module's Application class
 * implements BluetoothDependencies and is passed in as an instance at
 * build time, from BluetoothActivity.
 */
@Component(dependencies = [BluetoothDependencies::class], modules = [BluetoothDataModule::class])
interface BluetoothComponent {
    fun viewModelFactory(): BluetoothViewModelFactory
    fun gattExplorerViewModelFactory(): GattExplorerViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(dependencies: BluetoothDependencies): BluetoothComponent
    }
}
