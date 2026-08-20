package io.lb.bleandlistingopt.feature.bluetooth.data.real

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The classic senior-level BLE gotcha: every `BluetoothGatt` call
 * (`discoverServices`, `requestMtu`, `readCharacteristic`,
 * `writeDescriptor`, ...) is asynchronous -- it returns `true`/`false`
 * immediately ("did the request get queued to the radio") and the actual
 * result arrives later on a `BluetoothGattCallback` method. But the
 * underlying Bluetooth stack only processes ONE outstanding GATT operation
 * per connection at a time. If you fire a second call before the first
 * one's callback has arrived, the second call is dropped, returns `false`,
 * or -- worse -- silently corrupts the connection's internal state,
 * because the stack has nowhere to queue it.
 *
 * The fix is this queue: every GATT operation is wrapped in a suspend
 * function that (1) makes the call, (2) suspends on a `CompletableDeferred`
 * that only the matching `BluetoothGattCallback` method completes, and (3)
 * is itself run inside [withLock]. Because the wrapped operation doesn't
 * *return* until its callback has fired, the mutex isn't released until
 * then either -- so the next queued operation physically cannot start
 * until the previous one is done. One `Mutex`, one operation in flight,
 * exactly matching what the radio can actually do.
 */
class GattOperationQueue {
    private val mutex = Mutex()

    suspend fun <T> enqueue(operation: suspend () -> T): T = mutex.withLock { operation() }
}
