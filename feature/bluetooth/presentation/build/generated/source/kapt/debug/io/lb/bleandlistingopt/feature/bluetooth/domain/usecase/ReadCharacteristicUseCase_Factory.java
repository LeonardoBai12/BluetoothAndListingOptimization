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
public final class ReadCharacteristicUseCase_Factory implements Factory<ReadCharacteristicUseCase> {
  private final Provider<BleRepository> repositoryProvider;

  private ReadCharacteristicUseCase_Factory(Provider<BleRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ReadCharacteristicUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ReadCharacteristicUseCase_Factory create(
      Provider<BleRepository> repositoryProvider) {
    return new ReadCharacteristicUseCase_Factory(repositoryProvider);
  }

  public static ReadCharacteristicUseCase newInstance(BleRepository repository) {
    return new ReadCharacteristicUseCase(repository);
  }
}
