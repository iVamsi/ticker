pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // No repositoriesMode restriction: the Kotlin/JS plugin adds its own Node.js
    // distribution repository, which FAIL_ON_PROJECT_REPOS and PREFER_SETTINGS both break.
    repositories {
        mavenCentral()
    }
}

rootProject.name = "Ticker"

include(":ticker")
include(":ticker-coroutines")
