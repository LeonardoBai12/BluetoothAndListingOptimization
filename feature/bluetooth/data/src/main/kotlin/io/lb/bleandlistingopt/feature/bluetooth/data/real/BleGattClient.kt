package io.lb.bleandlistingopt.feature.bluetooth.data.real

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import io.lb.bleandlistingopt.core.common.Resource
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
private const val TARGET_MTU = 247
private const val DEFAULT_ATT_MTU = 23

/**
 * Owns exactly one **GATT** connection to one device.
 *
 * BLE vocabulary used throughout this class:
 * - **GATT** (Generic Attribute Profile): the protocol BLE devices speak
 *   once connected. `BluetoothGatt` is Android's client-side handle to it.
 * - **Service**: a named group of related values a peripheral exposes
 *   (e.g. "Heart Rate"), identified by a UUID. Fetched via
 *   `discoverServices()`.
 * - **Characteristic**: one readable/writable/notifiable value inside a
 *   service (e.g. "Heart Rate Measurement"), also identified by a UUID --
 *   this is what [readCharacteristic] reads and [enableNotifications]
 *   subscribes to.
 * - **Descriptor**: metadata attached to a characteristic. The one this
 *   class writes to, in [enableNotifications], is the "Client
 *   Characteristic Configuration" descriptor -- writing the magic value
 *   [BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE] to it is literally
 *   how a BLE client tells the peripheral "start pushing me updates for
 *   this characteristic".
 * - **MTU** (Maximum Transmission Unit): the largest number of bytes one
 *   BLE packet can carry. The default is a cramped 23 bytes (20 usable
 *   after protocol overhead), so [connect] negotiates a larger one via
 *   `requestMtu` before the connection is considered ready.
 *
 * Every call that talks to the radio (`discoverServices`, `requestMtu`,
 * `readCharacteristic`, `writeCharacteristic`, `writeDescriptor`) is routed through
 * [operationQueue] -- see [GattOperationQueue] for why that's required, not
 * optional, once more than one such call can happen in a connection's
 * lifetime.
 *
 * Permission checks happen in the presentation layer before any of this is
 * reached -- suppressed here rather than re-checked.
 */
@SuppressLint("MissingPermission")
class BleGattClient(
    private val context: Context,
    private val device: BluetoothDevice,
) {
    private val operationQueue = GattOperationQueue()
    private var gatt: BluetoothGatt? = null

    private var pendingConnection: CompletableDeferred<Resource<Unit>>? = null
    private var pendingServiceDiscovery: CompletableDeferred<Boolean>? = null
    private var pendingMtu: CompletableDeferred<Int>? = null
    private var pendingRead: CompletableDeferred<Resource<CharacteristicValue>>? = null
    private var pendingWrite: CompletableDeferred<Boolean>? = null
    private var pendingDescriptorWrite: CompletableDeferred<Boolean>? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState = _connectionState.asStateFlow()

    private val _notifications = MutableSharedFlow<CharacteristicValue>(extraBufferCapacity = 16)
    val notifications = _notifications.asSharedFlow()

    // Every override below takes its own `connectedGatt: BluetoothGatt` --
    // required by BluetoothGattCallback's signature, and deliberately named
    // instead of the single-letter `g` Android's own docs often use for it.
    // It shadows the outer `gatt` field on purpose: none of these callbacks
    // need the field, only the connection instance the system just handed
    // back for this specific event.
    private val callback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(connectedGatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    // Connected at the radio level, but service discovery
                    // and MTU negotiation (below, in connect()) still need
                    // to happen before this is usable -- Connecting, not
                    // Connected yet.
                    _connectionState.value = ConnectionState.Connecting
                    pendingConnection?.complete(Resource.Success(Unit))
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    _connectionState.value = ConnectionState.Disconnected
                    pendingConnection?.complete(Resource.Error("Disconnected, status=$status"))
                }
            }
        }

        override fun onServicesDiscovered(connectedGatt: BluetoothGatt, status: Int) {
            pendingServiceDiscovery?.complete(status == BluetoothGatt.GATT_SUCCESS)
        }

        override fun onMtuChanged(connectedGatt: BluetoothGatt, mtu: Int, status: Int) {
            pendingMtu?.complete(if (status == BluetoothGatt.GATT_SUCCESS) mtu else DEFAULT_ATT_MTU)
        }

        override fun onCharacteristicRead(
            connectedGatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int,
        ) {
            val result = if (status == BluetoothGatt.GATT_SUCCESS) {
                Resource.Success(CharacteristicValue(characteristic.uuid.toString(), value))
            } else {
                Resource.Error("Read failed, status=$status")
            }
            pendingRead?.complete(result)
        }

        override fun onCharacteristicWrite(
            connectedGatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int,
        ) {
            pendingWrite?.complete(status == BluetoothGatt.GATT_SUCCESS)
        }

        override fun onDescriptorWrite(connectedGatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            pendingDescriptorWrite?.complete(status == BluetoothGatt.GATT_SUCCESS)
        }

        override fun onCharacteristicChanged(
            connectedGatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            // Notifications arrive unsolicited -- there's no matching
            // request in the queue to pair them with, so they're just
            // published as they come in instead of resolving a deferred.
            _notifications.tryEmit(CharacteristicValue(characteristic.uuid.toString(), value))
        }
    }

    suspend fun connect(): Resource<Unit> {
        _connectionState.value = ConnectionState.Connecting
        val deferred = CompletableDeferred<Resource<Unit>>()
        pendingConnection = deferred
        gatt = device.connectGatt(context, false, callback)

        val connectResult = deferred.await()
        if (connectResult !is Resource.Success) return connectResult

        // connectGatt() itself isn't queued (there's nothing to serialize it
        // against yet -- it's what creates the connection these operations
        // need), but everything from here on is.
        val discovered = operationQueue.enqueue {
            val serviceDiscoveryResult = CompletableDeferred<Boolean>()
            pendingServiceDiscovery = serviceDiscoveryResult
            gatt?.discoverServices()
            serviceDiscoveryResult.await()
        }
        if (!discovered) return Resource.Error("Service discovery failed")

        val negotiatedMtu = operationQueue.enqueue {
            val mtuResult = CompletableDeferred<Int>()
            pendingMtu = mtuResult
            gatt?.requestMtu(TARGET_MTU)
            mtuResult.await()
        }

        _connectionState.value = ConnectionState.Connected(mtu = negotiatedMtu)
        return Resource.Success(Unit)
    }

    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
        _connectionState.value = ConnectionState.Disconnected
    }

    suspend fun readCharacteristic(serviceUuid: UUID, characteristicUuid: UUID): Resource<CharacteristicValue> =
        operationQueue.enqueue {
            val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
                ?: return@enqueue Resource.Error("Characteristic not found")

            val deferred = CompletableDeferred<Resource<CharacteristicValue>>()
            pendingRead = deferred
            val started = gatt?.readCharacteristic(characteristic) ?: false
            if (!started) return@enqueue Resource.Error("readCharacteristic() rejected -- radio busy or disconnected")
            deferred.await()
        }

    @Suppress("DEPRECATION") // the three-arg writeCharacteristic(characteristic, value, writeType) needs API 33; minSdk here is 24
    suspend fun writeCharacteristic(serviceUuid: UUID, characteristicUuid: UUID, value: ByteArray): Resource<Unit> =
        operationQueue.enqueue {
            val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
                ?: return@enqueue Resource.Error("Characteristic not found")

            val deferred = CompletableDeferred<Boolean>()
            pendingWrite = deferred
            characteristic.value = value
            val started = gatt?.writeCharacteristic(characteristic) ?: false
            if (!started) return@enqueue Resource.Error("writeCharacteristic() rejected -- radio busy or disconnected")
            if (deferred.await()) Resource.Success(Unit) else Resource.Error("Write failed")
        }

    @Suppress("DEPRECATION") // the two-arg writeDescriptor(descriptor, value) needs API 33; minSdk here is 24
    suspend fun enableNotifications(serviceUuid: UUID, characteristicUuid: UUID): Resource<Unit> =
        operationQueue.enqueue {
            val characteristic = gatt?.getService(serviceUuid)?.getCharacteristic(characteristicUuid)
                ?: return@enqueue Resource.Error("Characteristic not found")
            gatt?.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                ?: return@enqueue Resource.Error("Missing client characteristic config descriptor")

            val deferred = CompletableDeferred<Boolean>()
            pendingDescriptorWrite = deferred
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            val started = gatt?.writeDescriptor(descriptor) ?: false
            if (!started) return@enqueue Resource.Error("writeDescriptor() rejected")
            if (deferred.await()) Resource.Success(Unit) else Resource.Error("Enabling notifications failed")
        }
}
