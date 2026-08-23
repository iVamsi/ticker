plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

// mocha transitives in kotlin-js-store/yarn.lock. Dependabot cannot patch that
// lockfile (no real package.json). Drop these once the Kotlin plugin pins
// patched versions itself.
plugins.withType<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin> {
    the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
        resolution("diff", "8.0.3")
        resolution("serialize-javascript", "7.0.5")
    }
}
