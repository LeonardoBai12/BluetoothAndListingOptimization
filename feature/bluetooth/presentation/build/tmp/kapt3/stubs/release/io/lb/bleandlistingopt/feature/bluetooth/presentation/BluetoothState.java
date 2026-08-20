package io.lb.bleandlistingopt.feature.bluetooth.presentation;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BM\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010\u001a\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\bH\u00c6\u0003JO\u0010\u001d\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00062\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\bH\u00c6\u0001J\u0014\u0010\u001e\u001a\u00020\u00062\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010 \u001a\u00020!H\u00d6\u0081\u0004J\n\u0010\"\u001a\u00020\bH\u00d6\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0011R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u0011R\u0013\u0010\f\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013\u00a8\u0006#"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothState;", "", "devices", "", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/BleDevice;", "isScanning", "", "selectedAddress", "", "connectionState", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "isObservingNotifications", "lastReadValue", "<init>", "(Ljava/util/List;ZLjava/lang/String;Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;ZLjava/lang/String;)V", "getDevices", "()Ljava/util/List;", "()Z", "getSelectedAddress", "()Ljava/lang/String;", "getConnectionState", "()Lio/lb/bleandlistingopt/feature/bluetooth/domain/model/ConnectionState;", "getLastReadValue", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
public final class BluetoothState {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> devices = null;
    private final boolean isScanning = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String selectedAddress = null;
    @org.jetbrains.annotations.NotNull()
    private final io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState connectionState = null;
    private final boolean isObservingNotifications = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String lastReadValue = null;
    
    public BluetoothState(@org.jetbrains.annotations.NotNull()
    java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> devices, boolean isScanning, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedAddress, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState connectionState, boolean isObservingNotifications, @org.jetbrains.annotations.Nullable()
    java.lang.String lastReadValue) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> getDevices() {
        return null;
    }
    
    public final boolean isScanning() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSelectedAddress() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState getConnectionState() {
        return null;
    }
    
    public final boolean isObservingNotifications() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getLastReadValue() {
        return null;
    }
    
    public BluetoothState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> component1() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState component4() {
        return null;
    }
    
    public final boolean component5() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothState copy(@org.jetbrains.annotations.NotNull()
    java.util.List<io.lb.bleandlistingopt.feature.bluetooth.domain.model.BleDevice> devices, boolean isScanning, @org.jetbrains.annotations.Nullable()
    java.lang.String selectedAddress, @org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.domain.model.ConnectionState connectionState, boolean isObservingNotifications, @org.jetbrains.annotations.Nullable()
    java.lang.String lastReadValue) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}