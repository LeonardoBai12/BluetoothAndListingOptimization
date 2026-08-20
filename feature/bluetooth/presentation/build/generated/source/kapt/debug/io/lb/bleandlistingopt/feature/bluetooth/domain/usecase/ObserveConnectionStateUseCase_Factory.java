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
public final class ObserveConnectionStateUseCase_Factory implements Factory<ObserveConnectionStateUseCase> {
  private final Provider<BleRepository> repositoryProvider;

  private ObserveConnectionStateUseCase_Factory(Provider<BleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveConnectionStateUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveConnectionStateUseCase_Factory create(
      Provider<BleRepository> repositoryProvider) {
    return new ObserveConnectionStateUseCase_Factory(repositoryProvider);
  }

  public static ObserveConnectionStateUseCase newInstance(BleRepository repository) {
    return new ObserveConnectionStateUseCase(repository);
  }
}
