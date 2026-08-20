package io.lb.bleandlistingopt.feature.bluetooth.domain.model

data class BleDevice(
    val address: String,
    val name: String?,
    val rssi: Int,
)

sealed interface ConnectionState {
    data object Disconnected : ConnectionState
    data object Connecting : ConnectionState
    data class Connected(val mtu: Int) : ConnectionState
    data class Failed(val message: String?) : ConnectionState
}

class CharacteristicValue(
    val characteristicUuid: String,
    val bytes: ByteArray,
) {
    // ByteArray doesn't have structural equals/hashCode, so this class can't
    // be a data class without silently comparing by reference. Overridden
    // explicitly instead so callers (and tests) get the comparison they'd
    // expect from the type's name.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharacteristicValue) return false
        return characteristicUuid == other.characteristicUuid && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int = 31 * characteristicUuid.hashCode() + bytes.contentHashCode()
}
