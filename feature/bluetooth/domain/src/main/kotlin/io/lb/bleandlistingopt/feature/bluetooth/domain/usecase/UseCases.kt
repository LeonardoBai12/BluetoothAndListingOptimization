package io.lb.bleandlistingopt.feature.bluetooth.domain.usecase

import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class ScanForDevicesUseCase @Inject constructor(private val repository: BleRepository) {
    operator fun invoke(serviceUuid: String? = null): Flow<BleDevice> = repository.scanForDevices(serviceUuid)
}

class ConnectToDeviceUseCase @Inject constructor(private val repository: BleRepository) {
    suspend operator fun invoke(address: String): Resource<Unit> = repository.connect(address)
}

class DisconnectUseCase @Inject constructor(private val repository: BleRepository) {
    suspend operator fun invoke(address: String) = repository.disconnect(address)
}

class ObserveConnectionStateUseCase @Inject constructor(private val repository: BleRepository) {
    operator fun invoke(address: String): Flow<ConnectionState> = repository.observeConnectionState(address)
}

class ReadCharacteristicUseCase @Inject constructor(private val repository: BleRepository) {
    suspend operator fun invoke(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Resource<CharacteristicValue> = repository.readCharacteristic(address, serviceUuid, characteristicUuid)
}

class ObserveNotificationsUseCase @Inject constructor(private val repository: BleRepository) {
    operator fun invoke(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Flow<CharacteristicValue> = repository.observeNotifications(address, serviceUuid, characteristicUuid)
}
