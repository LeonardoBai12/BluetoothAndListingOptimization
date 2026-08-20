package io.lb.bleandlistingopt.feature.bluetooth.data.di;

/**
 * What this feature's Dagger graph needs from outside itself. The `:app`
 * module's Application class implements this (see its Dagger setup) and
 * hands the instance to [BluetoothComponent][io.lb.bleandlistingopt.feature.bluetooth.presentation.di.BluetoothComponent]'s
 * builder -- a `@Component(dependencies = [...])` edge, not a direct
 * `:app` -> `:feature:bluetooth:*` reference in either direction.
 */
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&\u00a8\u0006\u0004\u00c0\u0006\u0003"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/di/BluetoothDependencies;", "", "context", "Landroid/content/Context;", "BluetoothAndListingOptimization.feature.bluetooth:data_release"})
public abstract interface BluetoothDependencies {
    
    @org.jetbrains.annotations.NotNull()
    public abstract android.content.Context context();
}