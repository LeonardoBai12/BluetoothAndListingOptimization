package io.lb.bleandlistingopt.feature.bluetooth.data.real

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val scanner: BleScanner,
) : BleRepository {

    // One BleGattClient per connected address -- each owns its own
    // BluetoothGatt and its own GattOperationQueue, since GATT operations
    // only need to be serialized against each other *within* one
    // connection, not across independent connections to different devices.
    private val clients = mutableMapOf<String, BleGattClient>()

    override fun scanForDevices(serviceUuid: String?): Flow<BleDevice> = scanner.scan(serviceUuid)

    override fun observeConnectionState(address: String): Flow<ConnectionState> =
        clientFor(address).connectionState

    override suspend fun connect(address: String): Resource<Unit> = clientFor(address).connect()

    override suspend fun disconnect(address: String) {
        clients.remove(address)?.disconnect()
    }

    override suspend fun readCharacteristic(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Resource<CharacteristicValue> =
        clientFor(address).readCharacteristic(UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid))

    override suspend fun writeCharacteristic(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
        value: ByteArray,
    ): Resource<Unit> =
        clientFor(address).writeCharacteristic(UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), value)

    override fun observeNotifications(
        address: String,
        serviceUuid: String,
        characteristicUuid: String,
    ): Flow<CharacteristicValue> = flow {
        val client = clientFor(address)
        val enabled = client.enableNotifications(UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid))
        if (enabled !is Resource.Success) return@flow

        emitAll(client.notifications.filter { it.characteristicUuid == characteristicUuid })
    }

    private fun clientFor(address: String): BleGattClient = clients.getOrPut(address) {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val device = bluetoothManager.adapter.getRemoteDevice(address)
        BleGattClient(context, device)
    }
}
