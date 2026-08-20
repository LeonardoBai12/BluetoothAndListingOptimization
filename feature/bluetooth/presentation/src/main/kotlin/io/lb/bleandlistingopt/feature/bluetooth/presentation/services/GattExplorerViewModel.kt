package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import androidx.lifecycle.ViewModel
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.GetDiscoveredServicesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GattExplorerViewModel @Inject constructor(
    private val getDiscoveredServices: GetDiscoveredServicesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(GattExplorerState())
    val state: StateFlow<GattExplorerState> = _state.asStateFlow()

    // Called once from the Activity with the address of the device the
    // *other* screen already connected to -- discoverServices() already ran
    // as part of that connection, so this is just reading its result.
    fun load(address: String) {
        _state.value = GattExplorerState(address = address, services = getDiscoveredServices(address))
    }
}
