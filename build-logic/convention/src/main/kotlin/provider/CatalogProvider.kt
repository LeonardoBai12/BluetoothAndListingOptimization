package provider

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Shortcut to read the `libs` version catalog from inside a convention plugin,
 * mirroring what `libs.xxx` gives you for free in a regular build.gradle.kts.
 */
val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")
