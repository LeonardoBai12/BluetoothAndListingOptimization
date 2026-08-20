package io.lb.bleandlistingopt.feature.bluetooth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.WriteCharacteristicUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class BluetoothViewModel @Inject constructor(
    private val scanForDevices: ScanForDevicesUseCase,
    private val connectToDevice: ConnectToDeviceUseCase,
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val readCharacteristic: ReadCharacteristicUseCase,
    private val writeCharacteristic: WriteCharacteristicUseCase,
    private val observeNotifications: ObserveNotificationsUseCase,
    private val disconnectUseCase: DisconnectUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothState())
    val state: StateFlow<BluetoothState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<BluetoothEffect>()
    val effects: SharedFlow<BluetoothEffect> = _effects.asSharedFlow()

    private var scanJob: Job? = null
    private var notificationsJob: Job? = null

    fun onEvent(event: BluetoothEvent) {
        when (event) {
            BluetoothEvent.OnStartScan -> startScan()
            BluetoothEvent.OnStopScan -> stopScan()
            BluetoothEvent.OnToggleScanFilter -> toggleScanFilter()
            is BluetoothEvent.OnDeviceClick -> connect(event.address)
            BluetoothEvent.OnReadClick -> read()
            BluetoothEvent.OnResetEnergyClick -> resetEnergyExpended()
            BluetoothEvent.OnToggleNotifications -> toggleNotifications()
            BluetoothEvent.OnDisconnectClick -> disconnectDevice()
        }
    }

    private fun startScan() {
        stopScan()
        _state.update { it.copy(devices = emptyList(), isScanning = true) }
        val filterUuid = if (_state.value.scanFilterEnabled) HEART_RATE_SERVICE_UUID else null
        scanJob = viewModelScope.launch {
            scanForDevices(serviceUuid = filterUuid).collect { device ->
                _state.update { current ->
                    current.copy(devices = current.devices.filterNot { it.address == device.address } + device)
                }
            }
        }
    }

    private fun toggleScanFilter() {
        val wasScanning = _state.value.isScanning
        _state.update { it.copy(scanFilterEnabled = !it.scanFilterEnabled) }
        // Re-applies the new filter immediately if a scan is already
        // running, instead of only taking effect on the next "Start scan".
        if (wasScanning) startScan()
    }

    private fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _state.update { it.copy(isScanning = false) }
    }

    private fun connect(address: String) {
        stopScan()
        _state.update { it.copy(selectedAddress = address) }

        viewModelScope.launch {
            observeConnectionState(address).collect { connectionState ->
                _state.update { it.copy(connectionState = connectionState) }
            }
        }
        viewModelScope.launch {
            val result = connectToDevice(address)
            if (result is Resource.Error) {
                _effects.emit(BluetoothEffect.ShowError(result.message ?: "Connection failed"))
            }
        }
    }

    private fun read() {
        val address = _state.value.selectedAddress ?: return
        viewModelScope.launch {
            when (val result = readCharacteristic(address, HEART_RATE_SERVICE_UUID, HEART_RATE_MEASUREMENT_UUID)) {
                is Resource.Success -> _state.update { it.copy(lastReadValue = result.data.bytes.joinToString()) }
                is Resource.Error -> _effects.emit(BluetoothEffect.ShowError(result.message ?: "Read failed"))
                Resource.Loading -> Unit
            }
        }
    }

    // Heart Rate Control Point is the one writable characteristic in the
    // standard Heart Rate profile: writing 0x01 to it asks the peripheral to
    // reset its accumulated "Energy Expended" counter back to zero. Chosen
    // for this demo precisely because it's the only standard write target
    // this profile has -- everything else in it is read/notify-only.
    private fun resetEnergyExpended() {
        val address = _state.value.selectedAddress ?: return
        viewModelScope.launch {
            val result = writeCharacteristic(
                address,
                HEART_RATE_SERVICE_UUID,
                HEART_RATE_CONTROL_POINT_UUID,
                byteArrayOf(0x01),
            )
            if (result is Resource.Error) {
                _effects.emit(BluetoothEffect.ShowError(result.message ?: "Write failed"))
            }
        }
    }

    private fun toggleNotifications() {
        val currentJob = notificationsJob
        if (currentJob != null) {
            currentJob.cancel()
            notificationsJob = null
            _state.update { it.copy(isObservingNotifications = false) }
            return
        }

        val address = _state.value.selectedAddress ?: return
        _state.update { it.copy(isObservingNotifications = true) }
        notificationsJob = viewModelScope.launch {
            observeNotifications(address, HEART_RATE_SERVICE_UUID, HEART_RATE_MEASUREMENT_UUID).collect { value ->
                _state.update { it.copy(lastReadValue = value.bytes.joinToString()) }
            }
        }
    }

    private fun disconnectDevice() {
        val address = _state.value.selectedAddress ?: return
        notificationsJob?.cancel()
        notificationsJob = null
        viewModelScope.launch { disconnectUseCase(address) }
        _state.update {
            BluetoothState(devices = it.devices)
        }
    }

    override fun onCleared() {
        stopScan()
        notificationsJob?.cancel()
    }

    private companion object {
        // UUIDs for the standard Heart Rate service/characteristic, defined
        // by the Bluetooth SIG (Special Interest Group -- the industry
        // group that standardizes Bluetooth and assigns these well-known
        // UUIDs so any app can talk to any vendor's device implementing
        // them). Chosen here because nRF Connect's peripheral simulator
        // emulates this exact profile out of the box, so this screen is
        // testable against real hardware without writing a custom GATT
        // server (the peripheral side of GATT -- see BleGattClient's doc
        // comment for what GATT/service/characteristic mean).
        const val HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb"
        const val HEART_RATE_MEASUREMENT_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
        const val HEART_RATE_CONTROL_POINT_UUID = "00002a39-0000-1000-8000-00805f9b34fb"
    }
}
