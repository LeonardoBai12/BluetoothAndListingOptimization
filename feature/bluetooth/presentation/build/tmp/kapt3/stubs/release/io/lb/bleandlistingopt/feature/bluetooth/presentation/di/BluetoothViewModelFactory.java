package io.lb.bleandlistingopt.feature.bluetooth.presentation.di;

/**
 * Pure-Dagger ViewModel wiring (no Hilt): Dagger can't build a ViewModel
 * directly since `ViewModelProvider` insists on constructing it itself, so
 * this factory is the bridge -- it asks the Dagger graph (via [Provider],
 * which Dagger generates automatically for any @Inject type) for an
 * already-wired instance instead of calling `BluetoothViewModel()` itself.
 */
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001b\b\u0007\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u001a\u0002\b\u0007\u00a2\u0006\u0004\b\u0005\u0010\u0006J%\u0010\b\u001a\u0002H\t\"\b\b\u0000\u0010\t*\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\t0\fH\u0016\u00a2\u0006\u0002\u0010\rR\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000e"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/presentation/di/BluetoothViewModelFactory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "viewModelProvider", "Ljavax/inject/Provider;", "Lio/lb/bleandlistingopt/feature/bluetooth/presentation/BluetoothViewModel;", "<init>", "(Ljavax/inject/Provider;)V", "Ljavax/inject/Inject;", "create", "T", "Landroidx/lifecycle/ViewModel;", "modelClass", "Ljava/lang/Class;", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "BluetoothAndListingOptimization.feature.bluetooth:presentation_release"})
public final class BluetoothViewModelFactory implements androidx.lifecycle.ViewModelProvider.Factory {
    @org.jetbrains.annotations.NotNull()
    private final javax.inject.Provider<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel> viewModelProvider = null;
    
    @javax.inject.Inject()
    public BluetoothViewModelFactory(@org.jetbrains.annotations.NotNull()
    javax.inject.Provider<io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel> viewModelProvider) {
        super();
    }
    
    @java.lang.Override()
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
    java.lang.Class<T> modelClass) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
    java.lang.Class<T> modelClass, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.viewmodel.CreationExtras extras) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
    kotlin.reflect.KClass<T> modelClass, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.viewmodel.CreationExtras extras) {
        return null;
    }
}