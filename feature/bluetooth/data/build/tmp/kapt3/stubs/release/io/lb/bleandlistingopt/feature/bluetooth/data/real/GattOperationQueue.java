package io.lb.bleandlistingopt.feature.bluetooth.data.real;

/**
 * The classic senior-level BLE gotcha: every `BluetoothGatt` call
 * (`discoverServices`, `requestMtu`, `readCharacteristic`,
 * `writeDescriptor`, ...) is asynchronous -- it returns `true`/`false`
 * immediately ("did the request get queued to the radio") and the actual
 * result arrives later on a `BluetoothGattCallback` method. But the
 * underlying Bluetooth stack only processes ONE outstanding GATT operation
 * per connection at a time. If you fire a second call before the first
 * one's callback has arrived, the second call is dropped, returns `false`,
 * or -- worse -- silently corrupts the connection's internal state,
 * because the stack has nowhere to queue it.
 *
 * The fix is this queue: every GATT operation is wrapped in a suspend
 * function that (1) makes the call, (2) suspends on a `CompletableDeferred`
 * that only the matching `BluetoothGattCallback` method completes, and (3)
 * is itself run inside [withLock]. Because the wrapped operation doesn't
 * *return* until its callback has fired, the mutex isn't released until
 * then either -- so the next queued operation physically cannot start
 * until the previous one is done. One `Mutex`, one operation in flight,
 * exactly matching what the radio can actually do.
 */
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J2\u0010\u0006\u001a\u0002H\u0007\"\u0004\b\u0000\u0010\u00072\u001c\u0010\b\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00070\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\tH\u0086@\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/real/GattOperationQueue;", "", "<init>", "()V", "mutex", "Lkotlinx/coroutines/sync/Mutex;", "enqueue", "T", "operation", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "BluetoothAndListingOptimization.feature.bluetooth:data_release"})
public final class GattOperationQueue {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.sync.Mutex mutex = null;
    
    public GattOperationQueue() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final <T extends java.lang.Object>java.lang.Object enqueue(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super T>, ? extends java.lang.Object> operation, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super T> $completion) {
        return null;
    }
}