// settings.gradle.kts

// ============================================================
// Plugin Management configuration:
// This block configures the repositories Gradle uses to resolve plugins.
// We list gradlePluginPortal() first (to ensure plugins such as Kotlin Serialization are found),
// then google() and mavenCentral() for Android-specific and other plugins.
// ============================================================
pluginManagement {
    repositories {
        // The Gradle Plugin Portal is the primary source for community plugins
        gradlePluginPortal()
        // Google's repository for Android-specific plugins
        google()
        // Maven Central for additional plugins
        mavenCentral()
    }
}

// ============================================================
// Dependency Resolution Management:
// This block configures the repositories Gradle will use to resolve project dependencies.
// The repositoriesMode is set to FAIL_ON_PROJECT_REPOS to enforce the repositories defined here.
// ============================================================
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()       // Google's repository
        mavenCentral() // Maven Central repository
    }
}

// ============================================================
// Root project configuration
// ============================================================
rootProject.name = "Carol's Nest"
include(":app")
