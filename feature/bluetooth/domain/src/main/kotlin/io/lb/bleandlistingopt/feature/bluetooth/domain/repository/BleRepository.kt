package io.lb.bleandlistingopt.feature.bluetooth.domain.repository

import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun scanForDevices(): Flow<BleDevice>
    fun observeConnectionState(address: String): Flow<ConnectionState>
    suspend fun connect(address: String): Resource<Unit>
    suspend fun disconnect(address: String)

    suspend fun readCharacteristic(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Resource<CharacteristicValue>

    fun observeNotifications(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Flow<CharacteristicValue>
}
