package io.lb.bleandlistingopt.feature.bluetooth.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.lb.bleandlistingopt.feature.bluetooth.domain.repository.BleRepository;
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
public final class ScanForDevicesUseCase_Factory implements Factory<ScanForDevicesUseCase> {
  private final Provider<BleRepository> repositoryProvider;

  private ScanForDevicesUseCase_Factory(Provider<BleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ScanForDevicesUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ScanForDevicesUseCase_Factory create(Provider<BleRepository> repositoryProvider) {
    return new ScanForDevicesUseCase_Factory(repositoryProvider);
  }

  public static ScanForDevicesUseCase newInstance(BleRepository repository) {
    return new ScanForDevicesUseCase(repository);
  }
}
