package io.lb.bleandlistingopt.feature.bluetooth.data.real;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0015\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u001a\u0002\b\u0006\u00a2\u0006\u0004\b\u0004\u0010\u0005J\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00ca\u0001\u0010\b\u000b\u0012\f\b\f\u0012\b\b\fJ\u0004\b\b(\r\u00a8\u0006\n"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleScanner;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "Ljavax/inject/Inject;", "scan", "Lkotlinx/coroutines/flow/Flow;", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/BleDevice;", "BluetoothAndListingOptimization.feature.bluetooth:data_debug", "Landroid/annotation/SuppressLint;", "value", "MissingPermission"})
@android.annotation.SuppressLint(value = {"MissingPermission"})
public final class BleScanner {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @javax.inject.Inject()
    public BleScanner(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * A `callbackFlow` bridges the callback-based `BluetoothLeScanner` API
     * into coroutines: `trySend` inside [ScanCallback.onScanResult] pushes
     * each result into the flow, and [awaitClose] -- run when the collector
     * cancels or the flow otherwise ends -- is what stops the scan. Without
     * it, cancelling collection would leak an active scan (and drain the
     * battery) since nothing else calls `stopScan`.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> scan() {
        return null;
    }
}