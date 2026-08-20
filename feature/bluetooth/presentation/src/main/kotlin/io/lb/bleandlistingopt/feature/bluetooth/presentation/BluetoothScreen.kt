package io.lb.bleandlistingopt.feature.bluetooth.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState
import io.lb.bleandlistingopt.feature.bluetooth.presentation.services.GattExplorerActivity

/**
 * API 31+ requests BLUETOOTH_SCAN + BLUETOOTH_CONNECT; below that, scanning
 * needs ACCESS_FINE_LOCATION instead (BLUETOOTH/BLUETOOTH_ADMIN are
 * install-time permissions there, nothing to request for them). See the
 * manifest for why each is declared with a maxSdkVersion or not.
 */
private val bluetoothPermissions: Array<String> =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

@Composable
fun BluetoothScreen(viewModel: BluetoothViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var hasPermissions by remember {
        mutableStateOf(bluetoothPermissions.all { ContextCompat.checkSelfPermission(context, it) == android.content.pm.PackageManager.PERMISSION_GRANTED })
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results -> hasPermissions = results.values.all { it } }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BluetoothEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!hasPermissions) {
                Text("Bluetooth permissions are required to scan for devices.")
                Button(onClick = { permissionLauncher.launch(bluetoothPermissions) }) {
                    Text("Grant permissions")
                }
                return@Column
            }

            if (state.selectedAddress == null) {
                ScanSection(state = state, onEvent = viewModel::onEvent)
            } else {
                ConnectionSection(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}

@Composable
private fun ScanSection(state: BluetoothState, onEvent: (BluetoothEvent) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Filtered (default): scan only for devices advertising the Heart
        // Rate service -- the only ones this screen can read/notify from.
        // Off: an unfiltered scan, showing every BLE advertisement nearby.
        Text(if (state.scanFilterEnabled) "Filtro: Heart Rate" else "Filtro: desligado (mostra tudo)")
        Switch(checked = state.scanFilterEnabled, onCheckedChange = { onEvent(BluetoothEvent.OnToggleScanFilter) })
    }
    Button(onClick = { onEvent(if (state.isScanning) BluetoothEvent.OnStopScan else BluetoothEvent.OnStartScan) }) {
        Text(if (state.isScanning) "Stop scan" else "Start scan")
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.devices, key = { it.address }) { device ->
            Row(modifier = Modifier.fillMaxSize()) {
                Text(device.name ?: device.address)
                Button(onClick = { onEvent(BluetoothEvent.OnDeviceClick(device.address)) }) {
                    Text("Connect")
                }
            }
        }
    }
}

@Composable
private fun ConnectionSection(state: BluetoothState, onEvent: (BluetoothEvent) -> Unit) {
    val context = LocalContext.current
    Text("Address: ${state.selectedAddress}")
    Text("State: ${connectionStateLabel(state.connectionState)}")

    if (state.connectionState is ConnectionState.Connected) {
        Button(
            onClick = {
                val address = state.selectedAddress ?: return@Button
                context.startActivity(GattExplorerActivity.newIntent(context, address))
            },
        ) {
            Text("View GATT table")
        }
        Button(onClick = { onEvent(BluetoothEvent.OnReadClick) }) {
            Text("Read heart rate characteristic")
        }
        Button(onClick = { onEvent(BluetoothEvent.OnResetEnergyClick) }) {
            Text("Reset energy expended (write)")
        }
        Button(onClick = { onEvent(BluetoothEvent.OnToggleNotifications) }) {
            Text(if (state.isObservingNotifications) "Stop notifications" else "Start notifications")
        }
        state.lastReadValue?.let { Text("Last value: $it") }
    }

    Button(onClick = { onEvent(BluetoothEvent.OnDisconnectClick) }) {
        Text("Disconnect")
    }
}

private fun connectionStateLabel(state: ConnectionState): String = when (state) {
    ConnectionState.Disconnected -> "Disconnected"
    ConnectionState.Connecting -> "Connecting..."
    is ConnectionState.Connected -> "Connected (MTU ${state.mtu})"
    is ConnectionState.Failed -> "Failed: ${state.message}"
}
