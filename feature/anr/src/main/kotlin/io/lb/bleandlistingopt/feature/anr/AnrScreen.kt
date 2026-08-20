package io.lb.bleandlistingopt.feature.anr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Each row below is one anti-pattern/fix pair. Trigger buttons deliberately
 * freeze the UI for ~6s (or, for the deadlock pair, forever) -- that's the
 * point, not a bug in this screen. See the project README for how to pull
 * and read the resulting ANR trace, and how it maps to the "last ANR"
 * reason shown at the bottom, read via ActivityManager on this screen's
 * *next* launch.
 */
@Composable
fun AnrScreen(viewModel: AnrViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Status: ${state.status}")

            AnrPairRow(
                label = "1. Thread.sleep(6s)",
                onTrigger = { viewModel.onEvent(AnrEvent.OnTriggerSleep) },
                onFix = { viewModel.onEvent(AnrEvent.OnFixSleep) },
            )
            AnrPairRow(
                label = "2. CPU-bound loop",
                onTrigger = { viewModel.onEvent(AnrEvent.OnTriggerCpuLoop) },
                onFix = { viewModel.onEvent(AnrEvent.OnFixCpuLoop) },
            )
            AnrPairRow(
                label = "3. Blocking disk write",
                onTrigger = { viewModel.onEvent(AnrEvent.OnTriggerDiskRead) },
                onFix = { viewModel.onEvent(AnrEvent.OnFixDiskRead) },
            )
            AnrPairRow(
                label = "4. Two-lock deadlock",
                onTrigger = { viewModel.onEvent(AnrEvent.OnTriggerDeadlock) },
                onFix = { viewModel.onEvent(AnrEvent.OnFixDeadlock) },
            )

            Text("Last ANR (read on this launch): ${state.lastAnrReason ?: "none recorded"}")
        }
    }
}

@Composable
private fun AnrPairRow(label: String, onTrigger: () -> Unit, onFix: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onTrigger) { Text("Trigger ANR") }
            Button(onClick = onFix) { Text("Fixed version") }
        }
    }
}
