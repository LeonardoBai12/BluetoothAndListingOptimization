package io.lb.bleandlistingopt.feature.bluetooth.data.di;

/**
 * To run on an emulator with no Bluetooth adapter, swap the binding below
 * for `FakeBleRepository` (in `feature.bluetooth.data.fake`) instead of
 * `BleRepositoryImpl`.
 */
@dagger.Module()
@kotlin.Metadata(mv = {2, 4, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\b\'\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\'b\u0002\b\b\u00ca\u0001\u0002\b\n\u00a8\u0006\t"}, d2 = {"Lio/lb/bleandlistingopt/feature/bluetooth/data/di/BluetoothDataModule;", "", "<init>", "()V", "bindBleRepository", "Lio/lb/bleandlistingopt/feature/bluetooth/domain/repository/BleRepository;", "impl", "Lio/lb/bleandlistingopt/feature/bluetooth/data/real/BleRepositoryImpl;", "Ldagger/Binds;", "BluetoothAndListingOptimization.feature.bluetooth:data_release", "Ldagger/Module;"})
public abstract class BluetoothDataModule {
    
    public BluetoothDataModule() {
        super();
    }
    
    @dagger.Binds()
    @org.jetbrains.annotations.NotNull()
    public abstract io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository bindBleRepository(@org.jetbrains.annotations.NotNull()
    io.lb.bleandlistingopt.feature.bluetooth.data.real.BleRepositoryImpl impl);
}