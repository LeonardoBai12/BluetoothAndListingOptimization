plugins {
    alias(libs.plugins.lb.android.library)
}

android {
    namespace = "io.lb.bleandlistingopt.feature.listing.xml"

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
