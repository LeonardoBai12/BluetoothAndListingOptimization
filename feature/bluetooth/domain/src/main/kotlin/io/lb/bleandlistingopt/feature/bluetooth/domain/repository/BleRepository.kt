package io.lb.bleandlistingopt.feature.bluetooth.domain.repository

import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattServiceInfo
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun scanForDevices(serviceUuid: String? = null): Flow<BleDevice>
    fun observeConnectionState(address: String): Flow<ConnectionState>
    suspend fun connect(address: String): Resource<Unit>
    suspend fun disconnect(address: String)

    // Not suspend: by the time connect() has returned successfully,
    // discoverServices() has already run and BluetoothGatt is holding the
    // result in memory -- this just reads that cache, no new radio
    // operation, so it doesn't need the GATT queue either.
    fun discoveredServices(address: String): List<GattServiceInfo>

    suspend fun readCharacteristic(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Resource<CharacteristicValue>

    suspend fun writeCharacteristic(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
        value: ByteArray,
    ): Resource<Unit>

    fun observeNotifications(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Flow<CharacteristicValue>
}
