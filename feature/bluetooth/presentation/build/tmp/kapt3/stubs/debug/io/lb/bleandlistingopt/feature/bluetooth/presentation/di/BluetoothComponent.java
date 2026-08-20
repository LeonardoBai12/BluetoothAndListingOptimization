package io.lb.bleandlistingopt.feature.bluetooth.presentation.di;

/**
 * A component *dependency* (not a subcomponent): this graph only sees what
 * [BluetoothDependencies] exposes from the app-level graph, not that
 * graph's entire binding set. `:feature:bluetooth:presentation` never
 * references `:app` directly -- the app module's Application class
 * implements BluetoothDependencies and is passed in as an instance at
 * build time, from BluetoothActivity.
 */
@dagger.Component(dependencies = {io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies.class}, modules = {io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDataModule.class})
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\bg\u0018\u00002\u00020\u0001:\u0001\u0004J\b\u0010\u0002\u001a\u00020\u0003H&\u00ca\u0001\u001e\b\u0006\u0012\f\b\u0007\u0012\b\b\fJ\u0004\b\t0\b\u0012\f\b\t\u0012\b\b\fJ\u0004\b\t0\n\u00a8\u0006\u0005\u00c0\u0006\u0003"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/di/BluetoothComponent;", "", "viewModelFactory", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/di/BluetoothViewModelFactory;", "Factory", "BluetoothAndListingOptimization.feature.bluetooth:presentation_debug", "Ldagger/Component;", "dependencies", "Lio/lb/bleandlistingopt/feature/bluetooth/data/di/BluetoothDependencies;", "modules", "Lio/lb/bleandlistingopt/feature/bluetooth/data/di/BluetoothDataModule;"})
public abstract interface BluetoothComponent {
    
    @org.jetbrains.annotations.NotNull()
    public abstract io.lb.bleandlistingopt.feature.bluetooth.presentation.di.BluetoothViewModelFactory viewModelFactory();
    
    @dagger.Component.Factory()
    @kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&\u00ca\u0001\u0002\b\u0007\u00a8\u0006\u0006\u00c0\u0006\u0003"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/di/BluetoothComponent$Factory;", "", "create", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/di/BluetoothComponent;", "dependencies", "Lio/lb/bleandlistingopt/feature/bluetooth/data/di/BluetoothDependencies;", "BluetoothAndListingOptimization.feature.bluetooth:presentation_debug", "Ldagger/Component$Factory;"})
    public static abstract interface Factory {
        
        @org.jetbrains.annotations.NotNull()
        public abstract io.lb.bleandlistingopt.feature.bluetooth.presentation.di.BluetoothComponent create(@org.jetbrains.annotations.NotNull()
        io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies dependencies);
    }
}