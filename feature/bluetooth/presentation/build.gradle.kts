plugins {
    alias(libs.plugins.lb.android.library)
    alias(libs.plugins.lb.android.compose)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "io.lb.bleandlistingopt.feature.bluetooth.presentation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:bluetooth:domain"))
    // Wires the real BLE stack + the Dagger module that binds it. This
    // module needs FakeBleRepository too (for emulator-only runs), and both
    // live in :feature:bluetooth:data alongside the real implementation.
    implementation(project(":feature:bluetooth:data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.collections.immutable)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
