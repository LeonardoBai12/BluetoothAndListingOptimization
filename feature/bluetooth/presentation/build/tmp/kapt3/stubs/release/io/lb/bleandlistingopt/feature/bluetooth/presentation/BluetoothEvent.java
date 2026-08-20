package io.lb.bleandlistingopt.feature.bluetooth.presentation;

@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bv\u0018\u00002\u00020\u0001:\u0006\u0002\u0003\u0004\u0005\u0006\u0007\u0082\u0001\u0006\b\t\n\u000b\f\r\u00a8\u0006\u000e\u00c0\u0006\u0003"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "", "OnStartScan", "OnStopScan", "OnDeviceClick", "OnReadClick", "OnToggleNotifications", "OnDisconnectClick", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnDeviceClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnDisconnectClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnReadClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnStartScan;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnStopScan;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnToggleNotifications;", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
public abstract interface BluetoothEvent {
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fH\u00d6\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnDeviceClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "address", "", "<init>", "(Ljava/lang/String;)V", "getAddress", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnDeviceClick implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String address = null;
        
        public OnDeviceClick(@org.jetbrains.annotations.NotNull()
        java.lang.String address) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getAddress() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnDeviceClick copy(@org.jetbrains.annotations.NotNull()
        java.lang.String address) {
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
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0083\u0004J\n\u0010\b\u001a\u00020\tH\u00d6\u0081\u0004J\n\u0010\n\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnDisconnectClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnDisconnectClick implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        public static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnDisconnectClick INSTANCE = null;
        
        private OnDisconnectClick() {
            super();
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
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0083\u0004J\n\u0010\b\u001a\u00020\tH\u00d6\u0081\u0004J\n\u0010\n\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnReadClick;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnReadClick implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        public static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnReadClick INSTANCE = null;
        
        private OnReadClick() {
            super();
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
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0083\u0004J\n\u0010\b\u001a\u00020\tH\u00d6\u0081\u0004J\n\u0010\n\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnStartScan;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnStartScan implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        public static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnStartScan INSTANCE = null;
        
        private OnStartScan() {
            super();
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
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0083\u0004J\n\u0010\b\u001a\u00020\tH\u00d6\u0081\u0004J\n\u0010\n\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnStopScan;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnStopScan implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        public static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnStopScan INSTANCE = null;
        
        private OnStopScan() {
            super();
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
    
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u00c6\n\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00d6\u0083\u0004J\n\u0010\b\u001a\u00020\tH\u00d6\u0081\u0004J\n\u0010\n\u001a\u00020\u000bH\u00d6\u0081\u0004\u00a8\u0006\f"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent$OnToggleNotifications;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothEvent;", "<init>", "()V", "equals", "", "other", "", "hashCode", "", "toString", "", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
    public static final class OnToggleNotifications implements io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent {
        @org.jetbrains.annotations.NotNull()
        public static final io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothEvent.OnToggleNotifications INSTANCE = null;
        
        private OnToggleNotifications() {
            super();
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
}