package io.lb.bleandlistingopt.feature.bluetooth.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel
import javax.inject.Inject
import javax.inject.Provider

/**
 * Pure-Dagger ViewModel wiring (no Hilt): Dagger can't build a ViewModel
 * directly since `ViewModelProvider` insists on constructing it itself, so
 * this factory is the bridge -- it asks the Dagger graph (via [Provider],
 * which Dagger generates automatically for any @Inject type) for an
 * already-wired instance instead of calling `BluetoothViewModel()` itself.
 */
class BluetoothViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<BluetoothViewModel>,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModelProvider.get() as T
}
