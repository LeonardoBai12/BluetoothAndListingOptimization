package io.lb.bleandlistingopt.feature.bluetooth.data.di

import android.content.Context

/**
 * What this feature's Dagger graph needs from outside itself. The `:app`
 * module's Application class implements this (see its Dagger setup) and
 * hands the instance to [BluetoothComponent][io.lb.bleandlistingopt.feature.bluetooth.presentation.di.BluetoothComponent]'s
 * builder -- a `@Component(dependencies = [...])` edge, not a direct
 * `:app` -> `:feature:bluetooth:*` reference in either direction.
 */
interface BluetoothDependencies {
    fun context(): Context
}
