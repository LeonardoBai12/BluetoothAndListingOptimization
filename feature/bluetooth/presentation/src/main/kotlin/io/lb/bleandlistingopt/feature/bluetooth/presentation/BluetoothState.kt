package io.lb.bleandlistingopt.feature.bluetooth.presentation

import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState

data class BluetoothState(
    val devices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    val selectedAddress: String? = null,
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isObservingNotifications: Boolean = false,
    val lastReadValue: String? = null,
)

sealed interface BluetoothEvent {
    data object OnStartScan : BluetoothEvent
    data object OnStopScan : BluetoothEvent
    data class OnDeviceClick(val address: String) : BluetoothEvent
    data object OnReadClick : BluetoothEvent
    data object OnToggleNotifications : BluetoothEvent
    data object OnDisconnectClick : BluetoothEvent
}

sealed interface BluetoothEffect {
    data class ShowError(val message: String) : BluetoothEffect
}
