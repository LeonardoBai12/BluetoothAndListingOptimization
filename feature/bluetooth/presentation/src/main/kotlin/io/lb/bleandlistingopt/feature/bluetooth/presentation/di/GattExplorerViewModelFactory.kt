package io.lb.bleandlistingopt.feature.bluetooth.presentation.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.lb.bleandlistingopt.feature.bluetooth.presentation.services.GattExplorerViewModel
import javax.inject.Inject
import javax.inject.Provider

/** Same pure-Dagger pattern as [BluetoothViewModelFactory], one per screen -- see that class's doc comment. */
class GattExplorerViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<GattExplorerViewModel>,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModelProvider.get() as T
}
