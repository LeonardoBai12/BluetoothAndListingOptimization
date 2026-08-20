package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.GetDiscoveredServicesUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.WriteCharacteristicUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GattExplorerViewModel @Inject constructor(
    private val getDiscoveredServices: GetDiscoveredServicesUseCase,
    private val writeCharacteristic: WriteCharacteristicUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(GattExplorerState())
    val state: StateFlow<GattExplorerState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<GattExplorerEffect>()
    val effects: SharedFlow<GattExplorerEffect> = _effects.asSharedFlow()

    // Called once from the Activity with the address of the device the
    // *other* screen already connected to -- discoverServices() already ran
    // as part of that connection, so this is just reading its result.
    fun load(address: String) {
        _state.value = GattExplorerState(address = address, services = getDiscoveredServices(address))
    }

    fun onEvent(event: GattExplorerEvent) {
        when (event) {
            is GattExplorerEvent.OnWriteClick -> write(event.serviceUuid, event.characteristicUuid, event.hexValue)
        }
    }

    private fun write(serviceUuid: String, characteristicUuid: String, hexValue: String) {
        val bytes = hexValue.decodeHex()
        if (bytes == null) {
            viewModelScope.launch {
                _effects.emit(GattExplorerEffect.ShowWriteResult("Invalid hex -- use pairs like 01 0A FF"))
            }
            return
        }

        viewModelScope.launch {
            val result = writeCharacteristic(_state.value.address, serviceUuid, characteristicUuid, bytes)
            val message = when (result) {
                is Resource.Success -> "Wrote ${bytes.joinToString("") { "%02X".format(it) }} successfully"
                is Resource.Error -> result.message ?: "Write failed"
                Resource.Loading -> return@launch
            }
            _effects.emit(GattExplorerEffect.ShowWriteResult(message))
        }
    }
}

/**
 * "01 0A FF" or "010AFF" -> the matching bytes -- this is the manual,
 * arbitrary-byte-value input the guide's "test hypotheses manually" step
 * describes doing in nRF Connect, done here instead. Whitespace is only
 * accepted between pairs, never *inside* one, so "0 1" stays invalid rather
 * than silently becoming 0x01.
 */
private fun String.decodeHex(): ByteArray? {
    val cleaned = trim().split(Regex("\\s+")).joinToString("")
    if (cleaned.isEmpty() || cleaned.length % 2 != 0) return null
    return try {
        ByteArray(cleaned.length / 2) { i ->
            cleaned.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
    } catch (_: NumberFormatException) {
        null
    }
}
