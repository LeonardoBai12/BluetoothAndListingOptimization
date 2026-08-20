package io.lb.bleandlistingopt.feature.bluetooth.data.di

import dagger.Binds
import dagger.Module
import io.lb.bleandlistingopt.feature.bluetooth.data.real.BleRepositoryImpl
import io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository

@Module
abstract class BluetoothDataModule {
    @Binds
    abstract fun bindBleRepository(impl: BleRepositoryImpl): BleRepository
}
