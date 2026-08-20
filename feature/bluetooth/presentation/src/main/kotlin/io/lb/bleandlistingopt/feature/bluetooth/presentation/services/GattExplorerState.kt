package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattServiceInfo

/**
 * Its own package, its own State, separate from [io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState]
 * on purpose -- SRP: this screen's reason to change is "how the GATT table
 * is displayed", unrelated to the connect/read/notify screen's reasons to
 * change.
 */
data class GattExplorerState(
    val address: String = "",
    val services: List<GattServiceInfo> = emptyList(),
)

// This screen started read-only (browse the table, nothing to trigger), so
// it had no Event/Effect -- an empty sealed interface would have been
// over-engineering. Sending an arbitrary byte value to any WRITE
// characteristic (the "test hypotheses manually" step from the guide) is a
// real user action with a real one-shot result, which is exactly what
// Event/Effect exist for -- so they earned their place here once that
// capability was added, not before.
sealed interface GattExplorerEvent {
    data class OnWriteClick(val serviceUuid: String, val characteristicUuid: String, val hexValue: String) : GattExplorerEvent
}

sealed interface GattExplorerEffect {
    data class ShowWriteResult(val message: String) : GattExplorerEffect
}
