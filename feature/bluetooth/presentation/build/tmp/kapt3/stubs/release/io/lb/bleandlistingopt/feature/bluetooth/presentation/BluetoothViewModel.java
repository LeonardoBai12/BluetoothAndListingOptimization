package io.lb.bleandlistingopt.feature.bluetooth.presentation;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 /2\u00020\u0001:\u0001/B=\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u001a\u0002\b\u0010\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000e\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020%J\b\u0010&\u001a\u00020#H\u0002J\b\u0010\'\u001a\u00020#H\u0002J\u0010\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020*H\u0002J\b\u0010+\u001a\u00020#H\u0002J\b\u0010,\u001a\u00020#H\u0002J\b\u0010-\u001a\u00020#H\u0002J\b\u0010.\u001a\u00020#H\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00130\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001a0\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010 X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel;", "Landroidx/lifecycle/ViewModel;", "scanForDevices", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ScanForDevicesUseCase;", "connectToDevice", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ConnectToDeviceUseCase;", "observeConnectionState", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ObserveConnectionStateUseCase;", "readCharacteristic", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ReadCharacteristicUseCase;", "observeNotifications", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ObserveNotificationsUseCase;", "disconnectUseCase", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/DisconnectUseCase;", "<init>", "(Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ScanForDevicesUseCase;Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ConnectToDeviceUseCase;Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ObserveConnectionStateUseCase;Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ReadCharacteristicUseCase;Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/ObserveNotificationsUseCase;Lio/lb/bleandlistingopt/feature/bluetooth/domain/usecase/DisconnectUseCase;)V", "Ljavax/inject/Inject;", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "_effects", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEffect;", "effects", "Lkotlinx/coroutines/flow/SharedFlow;", "getEffects", "()Lkotlinx/coroutines/flow/SharedFlow;", "scanJob", "Lkotlinx/coroutines/Job;", "notificationsJob", "onEvent", "", "event", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "startScan", "stopScan", "connect", "address", "", "read", "toggleNotifications", "disconnectDevice", "onCleared", "Companion", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
public final class BluetoothViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase scanForDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase connectToDevice = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase observeConnectionState = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase readCharacteristic = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase observeNotifications = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase disconnectUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEffect> _effects = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEffect> effects = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job scanJob;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job notificationsJob;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String HEART_RATE_MEASUREMENT_UUID = "00002a37-0000-1000-8000-00805f9b34fb";
    @org.jetbrains.annotations.NotNull()
    private static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public BluetoothViewModel(@org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase scanForDevices, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase connectToDevice, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase observeConnectionState, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase readCharacteristic, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase observeNotifications, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase disconnectUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEffect> getEffects() {
        return null;
    }
    
    public final void onEvent(@org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent event) {
    }
    
    private final void startScan() {
    }
    
    private final void stopScan() {
    }
    
    private final void connect(java.lang.String address) {
    }
    
    private final void read() {
    }
    
    private final void toggleNotifications() {
    }
    
    private final void disconnectDevice() {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel$Companion;", "", "<init>", "()V", "HEART_RATE_SERVICE_UUID", "", "HEART_RATE_MEASUREMENT_UUID", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}