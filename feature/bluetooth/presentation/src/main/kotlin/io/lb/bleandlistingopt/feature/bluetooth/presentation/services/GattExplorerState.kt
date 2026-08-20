package io.lb.bleandlistingopt.feature.bluetooth.presentation.services

import io.lb.bleandlistingopt.feature.bluetooth.domain.model.GattServiceInfo

/**
 * Its own package, its own State, separate from [io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState]
 * on purpose -- SRP: this screen's reason to change is "how the GATT table
 * is displayed", unrelated to the connect/read/notify screen's reasons to
 * change. No Event/Effect here: this screen has nothing the user can
 * trigger besides loading once and looking -- adding an empty sealed
 * interface for events that don't exist would be the over-engineering the
 * MVI pattern is meant to avoid, not follow.
 */
data class GattExplorerState(
    val address: String = "",
    val services: List<GattServiceInfo> = emptyList(),
)
