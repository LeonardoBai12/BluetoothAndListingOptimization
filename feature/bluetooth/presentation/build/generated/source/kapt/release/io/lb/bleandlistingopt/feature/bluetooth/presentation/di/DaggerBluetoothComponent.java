package io.lb.bleandlistingopt.feature.bluetooth.presentation.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import io.lb.bleandlistingopt.feature.bluetooth.data.di.BluetoothDependencies;
import io.lb.bleandlistingopt.feature.bluetooth.data.real.BleRepositoryImpl;
import io.lb.bleandlistingopt.feature.bluetooth.data.real.BleRepositoryImpl_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.data.real.BleScanner;
import io.lb.bleandlistingopt.feature.bluetooth.data.real.BleScanner_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ConnectToDeviceUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.DisconnectUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveConnectionStateUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ObserveNotificationsUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ReadCharacteristicUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase;
import io.lb.bleandlistingopt.feature.bluetooth.domain.usecase.ScanForDevicesUseCase_Factory;
import io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel;
import io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel_Factory;
import javax.annotation.processing.Generated;

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
public final class DaggerBluetoothComponent {
  private DaggerBluetoothComponent() {
  }

  public static BluetoothComponent.Factory factory() {
    return new Factory();
  }

  private static final class Factory implements BluetoothComponent.Factory {
    @Override
    public BluetoothComponent create(BluetoothDependencies dependencies) {
      Preconditions.checkNotNull(dependencies);
      return new BluetoothComponentImpl(dependencies);
    }
  }

  private static final class BluetoothComponentImpl implements BluetoothComponent {
    private final BluetoothComponentImpl bluetoothComponentImpl = this;

    Provider<Context> contextProvider;

    Provider<BleScanner> bleScannerProvider;

    Provider<BleRepositoryImpl> bleRepositoryImplProvider;

    Provider<ScanForDevicesUseCase> scanForDevicesUseCaseProvider;

    Provider<ConnectToDeviceUseCase> connectToDeviceUseCaseProvider;

    Provider<ObserveConnectionStateUseCase> observeConnectionStateUseCaseProvider;

    Provider<ReadCharacteristicUseCase> readCharacteristicUseCaseProvider;

    Provider<ObserveNotificationsUseCase> observeNotificationsUseCaseProvider;

    Provider<DisconnectUseCase> disconnectUseCaseProvider;

    Provider<BluetoothViewModel> bluetoothViewModelProvider;

    BluetoothComponentImpl(BluetoothDependencies bluetoothDependenciesParam) {

      initialize(bluetoothDependenciesParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final BluetoothDependencies bluetoothDependenciesParam) {
      this.contextProvider = new ContextProvider(bluetoothDependenciesParam);
      this.bleScannerProvider = BleScanner_Factory.create(contextProvider);
      this.bleRepositoryImplProvider = BleRepositoryImpl_Factory.create(contextProvider, bleScannerProvider);
      this.scanForDevicesUseCaseProvider = ScanForDevicesUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.connectToDeviceUseCaseProvider = ConnectToDeviceUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.observeConnectionStateUseCaseProvider = ObserveConnectionStateUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.readCharacteristicUseCaseProvider = ReadCharacteristicUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.observeNotificationsUseCaseProvider = ObserveNotificationsUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.disconnectUseCaseProvider = DisconnectUseCase_Factory.create(((Provider) (bleRepositoryImplProvider)));
      this.bluetoothViewModelProvider = BluetoothViewModel_Factory.create(scanForDevicesUseCaseProvider, connectToDeviceUseCaseProvider, observeConnectionStateUseCaseProvider, readCharacteristicUseCaseProvider, observeNotificationsUseCaseProvider, disconnectUseCaseProvider);
    }

    @Override
    public BluetoothViewModelFactory viewModelFactory() {
      return new BluetoothViewModelFactory(bluetoothViewModelProvider);
    }

    private static final class ContextProvider implements Provider<Context> {
      private final BluetoothDependencies bluetoothDependencies;

      ContextProvider(BluetoothDependencies bluetoothDependencies) {
        this.bluetoothDependencies = bluetoothDependencies;
      }

      @Override
      public Context get() {
        return Preconditions.checkNotNullFromComponent(bluetoothDependencies.context());
      }
    }
  }
}
