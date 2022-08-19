pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "compose-hello-rust"
include(
    ":app:common",
    ":app:android",
    ":app:desktop",
    ":app:ios",
    ":app:macos",
)
include(":hello-rust")
includeBuild("hello-rust-action")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
