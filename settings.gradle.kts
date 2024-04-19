@file:Suppress("UnstableApiUsage")

rootProject.name = "compose-hellorust"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
// TODO    mother fucker, WorkQueue error throw in Iguana
// TODO   夭寿啦, Iguana 会存在testClasses的工作队列问题,所有build类型的module需要排除掉
// TODO    然后再build.gradle.kts中allProject注册一个testClasses的任务就可以了
gradle.startParameter.excludedTaskNames.addAll(listOf(
//    ":buildSrc:testClasses",
    ":rust_plugin:testClasses",
))

include(
    ":app:common",
    ":app:android",
    ":app:desktop",
    ":app:ios",
    ":app:macos"
)
include(":hellorust")
includeBuild("rust_plugin")

pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            mavenCentral()
            gradlePluginPortal()
            google {
                content {
                    includeGroupByRegex(".*google.*")
                    includeGroupByRegex(".*android.*")
                }
            }
            maven(url = "https://androidx.dev/storage/compose-compiler/repository")
            maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }
}

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral {
            content {
                excludeGroupByRegex(".*com.android.tools.*")
            }
        }
        google {
            content {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven {
            setUrl("https://dl.bintray.com/kotlin/kotlin-dev")
            content {
                excludeGroupByRegex(".*com.android.tools.*")
            }
        }
        maven {
            setUrl("https://dl.bintray.com/kotlin/kotlin-eap")
            content {
                excludeGroupByRegex(".*com.android.tools.*")
            }
        }
        maven {
            setUrl("https://jitpack.io")
            content {
                includeGroupByRegex("com.github.*")
                excludeGroupByRegex(".*com.android.tools.*")
            }
        }
    }
}
