package io.lb.bleandlistingopt.feature.bluetooth.presentation

import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState

data class BluetoothState(
    val devices: List<BleDevice> = emptyList(),
    val isScanning: Boolean = false,
    // true: scan filtered to devices advertising the Heart Rate service --
    // the only ones this screen can actually read/notify from. false: an
    // unfiltered scan, showing every BLE advertisement in range, useful to
    // see the difference a ScanFilter makes but mostly full of devices this
    // screen can't do anything with.
    val scanFilterEnabled: Boolean = true,
    val selectedAddress: String? = null,
    val connectionState: ConnectionState = ConnectionState.Disconnected,
    val isObservingNotifications: Boolean = false,
    val lastReadValue: String? = null,
)

sealed interface BluetoothEvent {
    data object OnStartScan : BluetoothEvent
    data object OnStopScan : BluetoothEvent
    data object OnToggleScanFilter : BluetoothEvent
    data class OnDeviceClick(val address: String) : BluetoothEvent
    data object OnReadClick : BluetoothEvent
    data object OnToggleNotifications : BluetoothEvent
    data object OnDisconnectClick : BluetoothEvent
}

sealed interface BluetoothEffect {
    data class ShowError(val message: String) : BluetoothEffect
}
