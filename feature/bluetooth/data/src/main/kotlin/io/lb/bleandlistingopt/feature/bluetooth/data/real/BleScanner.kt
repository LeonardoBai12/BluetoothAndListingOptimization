package io.lb.bleandlistingopt.feature.bluetooth.data.real

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID
import javax.inject.Inject

// Permission checks (BLUETOOTH_SCAN on API 31+, ACCESS_FINE_LOCATION below
// it) are the presentation layer's job, done before any use case reaches
// this class -- see feature:bluetooth:presentation. Suppressed here rather
// than re-checked, so the same rationale isn't repeated at every call site.
@SuppressLint("MissingPermission")
class BleScanner @Inject constructor(private val context: Context) {

    /**
     * A `callbackFlow` bridges the callback-based `BluetoothLeScanner` API
     * into coroutines: `trySend` inside [ScanCallback.onScanResult] pushes
     * each result into the flow, and [awaitClose] -- run when the collector
     * cancels or the flow otherwise ends -- is what stops the scan. Without
     * it, cancelling collection would leak an active scan (and drain the
     * battery) since nothing else calls `stopScan`.
     *
     * [serviceUuid], when non-null, becomes a [ScanFilter] so the radio
     * itself discards advertisements that don't declare that service --
     * without one, `startScan` returns every BLE advertisement in range
     * (every nearby TV, headset, printer...), most of which nothing in this
     * app can actually do anything with.
     */
    fun scan(serviceUuid: String? = null): Flow<BleDevice> = callbackFlow {
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val scanner = bluetoothManager?.adapter?.bluetoothLeScanner

        if (scanner == null) {
            close(IllegalStateException("Bluetooth is unavailable or disabled"))
            return@callbackFlow
        }

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(
                    BleDevice(
                        address = result.device.address,
                        name = result.device.name,
                        rssi = result.rssi,
                    ),
                )
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("BLE scan failed, errorCode=$errorCode"))
            }
        }

        val filters = serviceUuid?.let {
            listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(UUID.fromString(it))).build())
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        scanner.startScan(filters, settings, callback)

        awaitClose { scanner.stopScan(callback) }
    }
}
