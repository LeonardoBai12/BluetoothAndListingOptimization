pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BluetoothAndListingOptimization"

include(":app")

include(":core:common")
include(":core:designsystem")

include(":feature:listing:compose")
include(":feature:listing:xml")

include(":feature:bluetooth:domain")
include(":feature:bluetooth:data")
include(":feature:bluetooth:presentation")

include(":feature:anr")
