package io.lb.bleandlistingopt.feature.bluetooth.presentation;

@kotlin.Metadata(mv = {2, 4, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\b\u001a(\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00050\rH\u0003b\u0002\b\b\u001a(\u0010\u000f\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00050\rH\u0003b\u0002\b\b\u001a\u0010\u0010\u0010\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u0011H\u0002\"\u0016\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0003\u00a8\u0006\u0012"}, d2 = {"bluetoothPermissions", "", "", "[Ljava/lang/String;", "BluetoothScreen", "", "viewModel", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel;", "Landroidx/compose/runtime/Composable;", "ScanSection", "state", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState;", "onEvent", "Lkotlin/Function1;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "ConnectionSection", "connectionStateLabel", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "BluetoothAndListingOptimization.feature.bluetooth:presentation_debug"})
public final class BluetoothScreenKt {
    
    /**
     * API 31+ requests BLUETOOTH_SCAN + BLUETOOTH_CONNECT; below that, scanning
     * needs ACCESS_FINE_LOCATION instead (BLUETOOTH/BLUETOOTH_ADMIN are
     * install-time permissions there, nothing to request for them). See the
     * manifest for why each is declared with a maxSdkVersion or not.
     */
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String[] bluetoothPermissions = null;
    
    @androidx.compose.runtime.Composable()
    public static final void BluetoothScreen(@org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ScanSection(io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState state, kotlin.jvm.functions.Function1<? super io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent, kotlin.Unit> onEvent) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ConnectionSection(io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState state, kotlin.jvm.functions.Function1<? super io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent, kotlin.Unit> onEvent) {
    }
    
    private static final java.lang.String connectionStateLabel(io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState state) {
        return null;
    }
}