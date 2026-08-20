package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattCharacteristicInfo
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattServiceInfo

/**
 * The in-app version of what nRF Connect's GATT table view shows: every
 * service and characteristic this specific device advertised, plus each
 * characteristic's properties (READ/WRITE/NOTIFY/...) -- the *where you can
 * write*, not the *what the bytes mean*, which stays outside what GATT
 * itself can tell you (see the guide, "GATT diz onde escrever...").
 *
 * Every WRITE-capable characteristic gets a byte input + Send button --
 * the "test a byte-format hypothesis manually" step from the guide, done
 * here instead of in nRF Connect.
 */
@Composable
fun GattExplorerScreen(viewModel: GattExplorerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is GattExplorerEffect.ShowWriteResult -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text("GATT table -- ${state.address}") }
            items(state.services, key = { it.uuid }) { service ->
                ServiceItem(
                    service = service,
                    onWrite = { characteristicUuid, hex ->
                        viewModel.onEvent(GattExplorerEvent.OnWriteClick(service.uuid, characteristicUuid, hex))
                    },
                )
            }
        }
    }
}

@Composable
private fun ServiceItem(service: GattServiceInfo, onWrite: (characteristicUuid: String, hex: String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Service ${service.uuid}")
        service.characteristics.forEach { characteristic ->
            CharacteristicRow(characteristic = characteristic, onWrite = { hex -> onWrite(characteristic.uuid, hex) })
        }
    }
}

@Composable
private fun CharacteristicRow(characteristic: GattCharacteristicInfo, onWrite: (hex: String) -> Unit) {
    Column {
        Text("  • ${characteristic.uuid}  [${characteristic.properties.joinToString()}]")

        val canWrite = "WRITE" in characteristic.properties || "WRITE_NO_RESPONSE" in characteristic.properties
        if (canWrite) {
            var hexInput by remember(characteristic.uuid) { mutableStateOf("") }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = hexInput,
                    onValueChange = { hexInput = it },
                    placeholder = { Text("01 0A FF") },
                )
                Button(onClick = { onWrite(hexInput) }) {
                    Text("Send")
                }
            }
        }
    }
}
