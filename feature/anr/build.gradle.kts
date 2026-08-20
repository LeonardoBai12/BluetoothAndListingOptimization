plugins {
    alias(libs.plugins.lb.android.library)
    alias(libs.plugins.lb.android.compose)
}

android {
    namespace = "io.lb.bleandlistingopt.feature.anr"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
