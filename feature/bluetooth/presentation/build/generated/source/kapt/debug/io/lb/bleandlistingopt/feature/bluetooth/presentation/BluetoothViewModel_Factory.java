package io.lb.bleandlistingopt.feature.bluetooth.presentation;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class BluetoothViewModel_Factory implements Factory<BluetoothViewModel> {
  private final Provider<ScanForDevicesUseCase> scanForDevicesProvider;

  private final Provider<ConnectToDeviceUseCase> connectToDeviceProvider;

  private final Provider<ObserveConnectionStateUseCase> observeConnectionStateProvider;

  private final Provider<ReadCharacteristicUseCase> readCharacteristicProvider;

  private final Provider<ObserveNotificationsUseCase> observeNotificationsProvider;

  private final Provider<DisconnectUseCase> disconnectUseCaseProvider;

  private BluetoothViewModel_Factory(Provider<ScanForDevicesUseCase> scanForDevicesProvider,
      Provider<ConnectToDeviceUseCase> connectToDeviceProvider,
      Provider<ObserveConnectionStateUseCase> observeConnectionStateProvider,
      Provider<ReadCharacteristicUseCase> readCharacteristicProvider,
      Provider<ObserveNotificationsUseCase> observeNotificationsProvider,
      Provider<DisconnectUseCase> disconnectUseCaseProvider) {
    this.scanForDevicesProvider = scanForDevicesProvider;
    this.connectToDeviceProvider = connectToDeviceProvider;
    this.observeConnectionStateProvider = observeConnectionStateProvider;
    this.readCharacteristicProvider = readCharacteristicProvider;
    this.observeNotificationsProvider = observeNotificationsProvider;
    this.disconnectUseCaseProvider = disconnectUseCaseProvider;
  }

  @Override
  public BluetoothViewModel get() {
    return newInstance(scanForDevicesProvider.get(), connectToDeviceProvider.get(), observeConnectionStateProvider.get(), readCharacteristicProvider.get(), observeNotificationsProvider.get(), disconnectUseCaseProvider.get());
  }

  public static BluetoothViewModel_Factory create(
      Provider<ScanForDevicesUseCase> scanForDevicesProvider,
      Provider<ConnectToDeviceUseCase> connectToDeviceProvider,
      Provider<ObserveConnectionStateUseCase> observeConnectionStateProvider,
      Provider<ReadCharacteristicUseCase> readCharacteristicProvider,
      Provider<ObserveNotificationsUseCase> observeNotificationsProvider,
      Provider<DisconnectUseCase> disconnectUseCaseProvider) {
    return new BluetoothViewModel_Factory(scanForDevicesProvider, connectToDeviceProvider, observeConnectionStateProvider, readCharacteristicProvider, observeNotificationsProvider, disconnectUseCaseProvider);
  }

  public static BluetoothViewModel newInstance(ScanForDevicesUseCase scanForDevices,
      ConnectToDeviceUseCase connectToDevice, ObserveConnectionStateUseCase observeConnectionState,
      ReadCharacteristicUseCase readCharacteristic,
      ObserveNotificationsUseCase observeNotifications, DisconnectUseCase disconnectUseCase) {
    return new BluetoothViewModel(scanForDevices, connectToDevice, observeConnectionState, readCharacteristic, observeNotifications, disconnectUseCase);
  }
}
