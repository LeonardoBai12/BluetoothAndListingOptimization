import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "io.lb.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // compileOnly: these plugins are applied programmatically (pluginManager.apply(...))
    // by the convention plugins below, so their classes must be on this module's
    // classpath, but this module never runs Android/Compose code itself.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
}

// Every convention plugin this study project has, on purpose kept to exactly three:
// one for the app module, one for library/feature modules, one to layer Compose
// support on top of either. See the project README for why this list stays short.
gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "io.lb.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "io.lb.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "io.lb.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
    }
}
