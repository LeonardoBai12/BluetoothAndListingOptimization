package io.lb.bleandlistingopt.feature.bluetooth.presentation.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.lb.bleandlistingopt.feature.bluetooth.presentation.BluetoothViewModel;
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
public final class BluetoothViewModelFactory_Factory implements Factory<BluetoothViewModelFactory> {
  private final Provider<BluetoothViewModel> viewModelProvider;

  private BluetoothViewModelFactory_Factory(Provider<BluetoothViewModel> viewModelProvider) {
    this.viewModelProvider = viewModelProvider;
  }

  @Override
  public BluetoothViewModelFactory get() {
    return newInstance(viewModelProvider);
  }

  public static BluetoothViewModelFactory_Factory create(
      Provider<BluetoothViewModel> viewModelProvider) {
    return new BluetoothViewModelFactory_Factory(viewModelProvider);
  }

  public static BluetoothViewModelFactory newInstance(
      javax.inject.Provider<BluetoothViewModel> viewModelProvider) {
    return new BluetoothViewModelFactory(viewModelProvider);
  }
}
