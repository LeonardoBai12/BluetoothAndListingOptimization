// Deliberately NOT an Android library. Resource<T>, DispatcherProvider and
// the fake-data generator are plain Kotlin -- keeping this module pure JVM
// is what lets the equally-pure-JVM `feature:bluetooth:domain` module depend
// on it directly. An Android library (AAR) can only be consumed by another
// Android module, never by a plain JVM one, so if this module were an
// Android library, domain would be forced to become one too just to reach
// Resource<T>.
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    // Provides the real Dispatchers.Main (backed by the Android main looper)
    // for DispatcherProvider's default implementation. This is just a JVM
    // artifact -- depending on it does not require the Android Gradle plugin.
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
