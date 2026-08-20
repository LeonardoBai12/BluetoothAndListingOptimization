package io.lb.bleandlistingopt.feature.bluetooth.data.real;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u001a\u0002\b\b\u00a2\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0016J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u000e2\u0006\u0010\u0012\u001a\u00020\u000bH\u0016J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010\u0012\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\u0016J\u0016\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\u0016J,\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00142\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bH\u0096@\u00a2\u0006\u0002\u0010\u001cJ&\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00190\u000e2\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u000b2\u0006\u0010\u001b\u001a\u00020\u000bH\u0016J\u0010\u0010\u001e\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u000bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00ca\u0001\u0010\b \u0012\f\b!\u0012\b\b\fJ\u0004\b\b(\"\u00a8\u0006\u001f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleRepositoryImpl;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/repository/BleRepository;", "context", "Landroid/content/Context;", "scanner", "Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleScanner;", "<init>", "(Landroid/content/Context;Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleScanner;)V", "Ljavax/inject/Inject;", "clients", "", "", "Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleGattClient;", "scanForDevices", "Lkotlinx/coroutines/flow/Flow;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/BleDevice;", "observeConnectionState", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "address", "connect", "Lio/lb/bleandlistingopt/core/common/Resource;", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "disconnect", "readCharacteristic", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/CharacteristicValue;", "serviceUuid", "characteristicUuid", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeNotifications", "clientFor", "BluetoothAndListingOptimization.feature.bluetooth:data_debug", "Landroid/annotation/SuppressLint;", "value", "MissingPermission"})
@android.annotation.SuppressLint(value = {"MissingPermission"})
public final class BleRepositoryImpl implements io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.data.real.BleScanner scanner = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, io.lb.bleandlistingopt.feature.bluetooth.data.real.BleGattClient> clients = null;
    
    @javax.inject.Inject()
    public BleRepositoryImpl(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.data.real.BleScanner scanner) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> scanForDevices() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState> observeConnectionState(@org.jetbrains.annotations.NotNull()
    java.lang.String address) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object connect(@org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.lb.bleandlistingopt.core.common.Resource<kotlin.Unit>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object disconnect(@org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object readCharacteristic(@org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    java.lang.String serviceUuid, @org.jetbrains.annotations.NotNull()
    java.lang.String characteristicUuid, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.lb.bleandlistingopt.core.common.Resource<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue>> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.CharacteristicValue> observeNotifications(@org.jetbrains.annotations.NotNull()
    java.lang.String address, @org.jetbrains.annotations.NotNull()
    java.lang.String serviceUuid, @org.jetbrains.annotations.NotNull()
    java.lang.String characteristicUuid) {
        return null;
    }
    
    private final io.lb.bleandlistingopt.feature.bluetooth.data.real.BleGattClient clientFor(java.lang.String address) {
        return null;
    }
}