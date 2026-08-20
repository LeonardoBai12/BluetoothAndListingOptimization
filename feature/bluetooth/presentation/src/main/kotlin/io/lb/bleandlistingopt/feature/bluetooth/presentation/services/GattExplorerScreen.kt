package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattServiceInfo

/**
 * The in-app version of what nRF Connect's GATT table view shows: every
 * service and characteristic this specific device advertised, plus each
 * characteristic's properties (READ/WRITE/NOTIFY/...) -- the *where you can
 * write*, not the *what the bytes mean*, which stays outside what GATT
 * itself can tell you (see the guide, "GATT diz onde escrever...").
 */
@Composable
fun GattExplorerScreen(viewModel: GattExplorerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Text("GATT table -- ${state.address}") }
            items(state.services, key = { it.uuid }) { service -> ServiceItem(service) }
        }
    }
}

@Composable
private fun ServiceItem(service: GattServiceInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Service ${service.uuid}")
        service.characteristics.forEach { characteristic ->
            Text("  • ${characteristic.uuid}  [${characteristic.properties.joinToString()}]")
        }
    }
}
