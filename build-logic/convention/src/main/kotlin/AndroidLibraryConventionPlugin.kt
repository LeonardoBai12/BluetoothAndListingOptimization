import com.android.build.api.dsl.LibraryExtension
import extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Convention plugin for `core:*` and `feature:*` modules: applies the
 * Android Library Gradle plugin + Kotlin, then layers the shared
 * compileSdk/minSdk/compileOptions block from [configureKotlinAndroid] on
 * top. Compose is deliberately NOT wired here — see
 * [AndroidComposeConventionPlugin], applied only where Compose is used.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }
        }
    }
}
