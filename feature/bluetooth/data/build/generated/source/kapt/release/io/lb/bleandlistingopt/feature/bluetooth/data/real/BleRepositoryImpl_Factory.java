package io.lb.bleandlistingopt.feature.bluetooth.data.real;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class BleRepositoryImpl_Factory implements Factory<BleRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<BleScanner> scannerProvider;

  private BleRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<BleScanner> scannerProvider) {
    this.contextProvider = contextProvider;
    this.scannerProvider = scannerProvider;
  }

  @Override
  public BleRepositoryImpl get() {
    return newInstance(contextProvider.get(), scannerProvider.get());
  }

  public static BleRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<BleScanner> scannerProvider) {
    return new BleRepositoryImpl_Factory(contextProvider, scannerProvider);
  }

  public static BleRepositoryImpl newInstance(Context context, BleScanner scanner) {
    return new BleRepositoryImpl(context, scanner);
  }
}
