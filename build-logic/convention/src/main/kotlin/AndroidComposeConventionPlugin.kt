import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import provider.libs

/**
 * Adds Compose support on top of [AndroidApplicationConventionPlugin] or
 * [AndroidLibraryConventionPlugin]. Kept as its own plugin (rather than
 * folded into the library plugin) because most feature modules in this
 * project are plain Views/XML and paying Compose's compiler + dependency
 * cost there would be pure waste.
 *
 * `extensions.configure<CommonExtension>` works here even though neither
 * ApplicationExtension nor LibraryExtension is literally "CommonExtension":
 * Gradle's extension container resolves by the requested type being
 * assignable from what's registered, and both of those extend
 * CommonExtension. That's what lets this one plugin flip the `compose`
 * feature flag regardless of which of the two was applied first. Note this
 * sets `buildFeatures.compose` as a property, not `buildFeatures { compose =
 * true }` as a block -- AGP 9's CommonExtension only exposes the shared
 * `getBuildFeatures()` property, the block-style `buildFeatures { ... }`
 * function is redeclared per concrete extension type and isn't visible here.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<CommonExtension> {
                buildFeatures.compose = true
            }

            dependencies {
                val bom = libs.findLibrary("androidx-compose-bom").get()
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                add("implementation", libs.findLibrary("androidx-compose-ui").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-graphics").get())
                add("implementation", libs.findLibrary("androidx-compose-ui-tooling-preview").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-test-manifest").get())
            }
        }
    }
}
