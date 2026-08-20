plugins {
    alias(libs.plugins.lb.android.library)
    alias(libs.plugins.lb.android.compose)
}

android {
    namespace = "io.lb.bleandlistingopt.core.designsystem"
}

dependencies {
    // api, not implementation: every module that depends on designsystem for
    // the theme also builds Compose screens and needs these directly.
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.foundation)
}
