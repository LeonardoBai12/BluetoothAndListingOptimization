package io.lb.bleandlistingopt.feature.bluetooth.data.real;

/**
 * Owns exactly one **GATT** connection to one device.
 *
 * BLE vocabulary used throughout this class:
 * - **GATT** (Generic Attribute Profile): the protocol BLE devices speak
 *  once connected. `BluetoothGatt` is Android's client-side handle to it.
 * - **Service**: a named group of related values a peripheral exposes
 *  (e.g. "Heart Rate"), identified by a UUID. Fetched via
 *  `discoverServices()`.
 * - **Characteristic**: one readable/writable/notifiable value inside a
 *  service (e.g. "Heart Rate Measurement"), also identified by a UUID --
 *  this is what [readCharacteristic] reads and [enableNotifications]
 *  subscribes to.
 * - **Descriptor**: metadata attached to a characteristic. The one this
 *  class writes to, in [enableNotifications], is the "Client
 *  Characteristic Configuration" descriptor -- writing the magic value
 *  [BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE] to it is literally
 *  how a BLE client tells the peripheral "start pushing me updates for
 *  this characteristic".
 * - **MTU** (Maximum Transmission Unit): the largest number of bytes one
 *  BLE packet can carry. The default is a cramped 23 bytes (20 usable
 *  after protocol overhead), so [connect] negotiates a larger one via
 *  `requestMtu` before the connection is considered ready.
 *
 * Every call that talks to the radio (`discoverServices`, `requestMtu`,
 * `readCharacteristic`, `writeDescriptor`) is routed through
 * [operationQueue] -- see [GattOperationQueue] for why that's required, not
 * optional, once more than one such call can happen in a connection's
 * lifetime.
 *
 * Permission checks happen in the presentation layer before any of this is
 * reached -- suppressed here rather than re-checked.
 */
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u0014\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0086@\u00a2\u0006\u0002\u0010(J\u0006\u0010)\u001a\u00020\u000fJ$\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00150\u000e2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020,H\u0086@\u00a2\u0006\u0002\u0010.J$\u0010/\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020,H\u0086@\u00a2\u0006\u0002\u0010.R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0014\u001a\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000e\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00190\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00150\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00150!\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0010\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010&\u00ca\u0001\u0010\b1\u0012\f\b2\u0012\b\b\fJ\u0004\b\b(3\u00a8\u00060"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient;", "", "context", "Landroid/content/Context;", "device", "Landroid/bluetooth/BluetoothDevice;", "<init>", "(Landroid/content/Context;Landroid/bluetooth/BluetoothDevice;)V", "operationQueue", "Lio/lb/bleandlistingopt/feature/bluetooth/data/real/GattOperationQueue;", "gatt", "Landroid/bluetooth/BluetoothGatt;", "pendingConnection", "Lkotlinx/coroutines/CompletableDeferred;", "Lio/lb/bleandlistingopt/core/common/Resource;", "", "pendingServiceDiscovery", "", "pendingMtu", "", "pendingRead", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/CharacteristicValue;", "pendingDescriptorWrite", "_connectionState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "connectionState", "Lkotlinx/coroutines/flow/StateFlow;", "getConnectionState", "()Lkotlinx/coroutines/flow/StateFlow;", "_notifications", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "notifications", "Lkotlinx/coroutines/flow/SharedFlow;", "getNotifications", "()Lkotlinx/coroutines/flow/SharedFlow;", "callback", "Landroid/bluetooth/BluetoothGattCallback;", "Landroid/bluetooth/BluetoothGattCallback;", "connect", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "disconnect", "readCharacteristic", "serviceUuid", "Ljava/util/UUID;", "characteristicUuid", "(Ljava/util/UUID;Ljava/util/UUID;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "enableNotifications", "BluetoothAndListingOptimization.feature.bluetooth:data_debug", "Landroid/annotation/SuppressLint;", "value", "MissingPermission"})
@android.annotation.SuppressLint(value = {"MissingPermission"})
public final class BleGattClient {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.bluetooth.BluetoothDevice device = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.data.real.GattOperationQueue operationQueue = null;
    @org.jetbrains.annotations.Nullable()
    private android.bluetooth.BluetoothGatt gatt;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CompletableDeferred<io.lb.bleandlistingopt.core.common.Resource<kotlin.Unit>> pendingConnection;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CompletableDeferred<java.lang.Boolean> pendingServiceDiscovery;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CompletableDeferred<java.lang.Integer> pendingMtu;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CompletableDeferred<io.lb.bleandlistingopt.core.common.Resource<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue>> pendingRead;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CompletableDeferred<java.lang.Boolean> pendingDescriptorWrite;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState> _connectionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState> connectionState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue> _notifications = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue> notifications = null;
    @org.jetbrains.annotations.NotNull()
    private final android.bluetooth.BluetoothGattCallback callback = null;
    
    public BleGattClient(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.bluetooth.BluetoothDevice device) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState> getConnectionState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue> getNotifications() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object connect(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.lb.bleandlistingopt.core.common.Resource<kotlin.Unit>> $completion) {
        return null;
    }
    
    public final void disconnect() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object readCharacteristic(@org.jetbrains.annotations.NotNull()
    java.util.UUID serviceUuid, @org.jetbrains.annotations.NotNull()
    java.util.UUID characteristicUuid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.lb.bleandlistingopt.core.common.Resource<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue>> $completion) {
        return null;
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object enableNotifications(@org.jetbrains.annotations.NotNull()
    java.util.UUID serviceUuid, @org.jetbrains.annotations.NotNull()
    java.util.UUID characteristicUuid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.lb.bleandlistingopt.core.common.Resource<kotlin.Unit>> $completion) {
        return null;
    }
}