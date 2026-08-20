package extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import provider.libs

/**
 * JVM target shared by every module. 17 because that's what AGP 9's minimum
 * supported Gradle/JDK combination requires for the build itself; app code
 * still runs down to [android-minSdk] via desugared bytecode, unrelated to
 * this compiler setting.
 */
private val COMPILE_VERSION = JavaVersion.VERSION_17

/**
 * The `compileSdk`/`minSdk`/compile-options wiring shared by every Android
 * module (app and library alike) so every module targets the exact same
 * platform version — this is what the "android application" and
 * "android library" convention plugins both delegate to instead of
 * duplicating the block.
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension,
) {
    // AGP 9 dropped CommonExtension's generic type parameters, and with them
    // the `defaultConfig { ... }` / `compileOptions { ... }` block-DSL sugar
    // (that sugar now only exists on the concrete ApplicationExtension /
    // LibraryExtension, each typed to its own DefaultConfig subtype). The
    // property getters are still shared, so this configures each nested
    // object directly instead of through a block.
    commonExtension.compileSdk = libs.findVersion("android-compileSdk").get().toString().toInt()

    commonExtension.defaultConfig.apply {
        minSdk = libs.findVersion("android-minSdk").get().toString().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    commonExtension.compileOptions.apply {
        sourceCompatibility = COMPILE_VERSION
        targetCompatibility = COMPILE_VERSION
    }

    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    tasks.withType<Test> {
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
