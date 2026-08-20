// Deliberately NOT an Android library. The domain layer only deals in plain
// Kotlin models, a repository interface and use cases -- nothing here
// touches android.bluetooth.* or any other framework class, so a pure JVM
// module is enough. That also makes its unit tests run as fast local JVM
// tests with no Robolectric/instrumentation needed.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
    // @Inject on use case constructors, so Dagger can provide them without a
    // manual @Provides per use case -- this is the only DI-related
    // dependency domain takes on, and it's a plain JSR-330 annotation
    // artifact, not Dagger itself.
    implementation(libs.javax.inject)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
}
