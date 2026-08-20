package io.lb.bleandlistingopt.feature.bluetooth.data.fake;

/**
 * No real Bluetooth radio involved -- bind this instead of
 * [io.lb.bleandlistingopt.feature.bluetooth.data.real.BleRepositoryImpl] on
 * an emulator, where there's no adapter to scan or connect with. Real
 * hardware testing needs a physical device and a BLE peripheral (e.g. the
 * nRF Connect app running in peripheral mode on a second phone).
 */
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\rH\u0016J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000b0\r2\u0006\u0010\u000f\u001a\u00020\tH\u0016J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u000f\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0013J,\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u00112\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\tH\u0096@\u00a2\u0006\u0002\u0010\u0019J&\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00160\r2\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\tH\u0016J\u0016\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u000f\u001a\u00020\tH\u0002R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0007\u001a\u0014\u0012\u0004\u0012\u00020\t\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/fake/FakeBleRepository;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/repository/BleRepository;", "<init>", "()V", "fakeDevices", "", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/BleDevice;", "connectionStates", "", "", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "scanForDevices", "Lkotlinx/coroutines/flow/Flow;", "observeConnectionState", "address", "connect", "Lio/lb/bleandlistingopt/core/common/Resource;", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "disconnect", "readCharacteristic", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/CharacteristicValue;", "serviceUuid", "characteristicUuid", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "observeNotifications", "stateFlowFor", "Companion", "BluetoothAndListingOptimization.feature.bluetooth:data_release"})
public final class FakeBleRepository implements io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> fakeDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, kotlinx.coroutines.flow.MutableStateFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState>> connectionStates = null;
    @java.lang.Deprecated()
    public static final long SCAN_EMIT_DELAY_MILLIS = 400L;
    @java.lang.Deprecated()
    public static final long CONNECT_DELAY_MILLIS = 600L;
    @java.lang.Deprecated()
    public static final long READ_DELAY_MILLIS = 150L;
    @java.lang.Deprecated()
    public static final long NOTIFICATION_INTERVAL_MILLIS = 1000L;
    @java.lang.Deprecated()
    public static final int FAKE_MTU = 247;
    @org.jetbrains.annotations.NotNull()
    private static final io.lb.bleandlistingopt.feature.bluetooth.data.fake.FakeBleRepository.Companion Companion = null;
    
    public FakeBleRepository() {
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
    
    private final kotlinx.coroutines.flow.MutableStateFlow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState> stateFlowFor(java.lang.String address) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/fake/FakeBleRepository$Companion;", "", "<init>", "()V", "SCAN_EMIT_DELAY_MILLIS", "", "CONNECT_DELAY_MILLIS", "READ_DELAY_MILLIS", "NOTIFICATION_INTERVAL_MILLIS", "FAKE_MTU", "", "BluetoothAndListingOptimization.feature.bluetooth:data_release"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}