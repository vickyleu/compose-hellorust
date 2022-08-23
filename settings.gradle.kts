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

rootProject.name = "compose-hellorust"
include(
    ":app:common",
    ":app:android",
    ":app:desktop",
    ":app:ios",
    ":app:macos"
)
include(":hellorust")
includeBuild("hellorust-plugin")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
