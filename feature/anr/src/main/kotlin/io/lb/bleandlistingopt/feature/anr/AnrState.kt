package io.lb.bleandlistingopt.feature.anr

data class AnrState(
    val status: String = "Idle",
    val lastAnrReason: String? = null,
)

sealed interface AnrEvent {
    data object OnTriggerSleep : AnrEvent
    data object OnFixSleep : AnrEvent
    data object OnTriggerCpuLoop : AnrEvent
    data object OnFixCpuLoop : AnrEvent
    data object OnTriggerDiskRead : AnrEvent
    data object OnFixDiskRead : AnrEvent
    data object OnTriggerDeadlock : AnrEvent
    data object OnFixDeadlock : AnrEvent
}

sealed interface AnrEffect {
    data class ShowMessage(val message: String) : AnrEffect
}
