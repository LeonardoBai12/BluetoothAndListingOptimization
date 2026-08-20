package io.lb.bleandlistingopt.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Indirection over [Dispatchers] so ViewModels/repositories can be unit
 * tested with a single-threaded fake instead of the real Main/IO/Default
 * dispatchers (see [FakeDispatcherProvider] usages in tests).
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
}
