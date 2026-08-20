plugins {
    alias(libs.plugins.lb.android.application)
    alias(libs.plugins.lb.android.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "io.lb.bleandlistingopt"

    defaultConfig {
        applicationId = "io.lb.bleandlistingopt"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":feature:listing:compose"))
    implementation(project(":feature:listing:xml"))
    implementation(project(":feature:bluetooth:data"))
    implementation(project(":feature:bluetooth:presentation"))
    implementation(project(":feature:anr"))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
