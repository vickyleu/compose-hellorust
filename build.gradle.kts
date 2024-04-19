import org.jetbrains.kotlin.gradle.dsl.JvmTarget
// 需要判断是否是jitpack的构建，如果是jitpack的构建，需要将build目录设置到项目根目录下
if (System.getenv("JITPACK") == null) {
    rootProject.layout.buildDirectory.set(file("./build"))
}
plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.spotless)
}
val javaVersion = JavaVersion.toVersion(libs.versions.jvmTarget.get())
check(JavaVersion.current().isCompatibleWith(javaVersion)) {
    "This project needs to be run with Java ${javaVersion.getMajorVersion()} or higher (found: ${JavaVersion.current()})."
}

subprojects {
    if (System.getenv("JITPACK") == null) {
        this.layout.buildDirectory.set(file("${rootProject.layout.buildDirectory.get().asFile.absolutePath}/${project.name}"))
    }
}

allprojects {

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        this.compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
            freeCompilerArgs.addAll(listOf("-opt-in=kotlin.RequiresOptIn", "-Xexpect-actual-classes"))
            allWarningsAsErrors.set(false)
        }
    }

    /*configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
                using(module("androidx.compose.compiler:compiler:${"1.3.0"}"))
            }
        }
    }*/

    apply(plugin = "com.diffplug.spotless")
    spotless {
        kotlin {
            target("**/*.kt")
            targetExclude(
                "${layout.buildDirectory.get().asFile.absolutePath}/**/*.kt",
                "bin/**/*.kt",
                "buildSrc/**/*.kt"
            )
            ktlint("0.46.1").editorConfigOverride(
                mapOf(
                    // rules: https://github.com/pinterest/ktlint/blob/master/README.md#standard-rules
                    "disabled_rules" to "filename,trailing-comma"
                )
            )
        }
        kotlinGradle {
            target("*.gradle.kts")
            ktlint("0.46.1")
        }
        java {
            target("**/*.java")
            targetExclude(
                "${layout.buildDirectory.get().asFile.absolutePath}/**/*.java",
                "bin/**/*.java"
            )
        }
    }
}
