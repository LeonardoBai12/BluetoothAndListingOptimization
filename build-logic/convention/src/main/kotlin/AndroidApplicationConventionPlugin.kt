import com.android.build.api.dsl.ApplicationExtension
import extensions.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import provider.libs

/**
 * Convention plugin for the single `:app` module: applies the Android
 * Application Gradle plugin + Kotlin, then layers the shared compileSdk/
 * minSdk/compileOptions block from [configureKotlinAndroid] on top.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)

                defaultConfig {
                    targetSdk = libs.findVersion("android-targetSdk").get().toString().toInt()
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }
            }
        }
    }
}
