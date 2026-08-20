plugins {
    alias(libs.plugins.lb.android.library)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "io.lb.bleandlistingopt.feature.bluetooth.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":feature:bluetooth:domain"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dagger is wired explicitly in every module that needs it -- no
    // convention plugin hides this, on purpose, so the DI setup stays
    // visible for study.
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
