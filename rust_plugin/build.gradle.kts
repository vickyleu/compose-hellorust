plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies {
    implementation("com.android.tools.build:gradle:${libs.versions.agp.get()}")
    implementation(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
}

kotlin{
    target {
        this.compilations.all {
            this.kotlinOptions.jvmTarget = libs.versions.jvmTarget.get()
        }
    }
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(libs.versions.kotlin.get())
            } else if (requested.group == "org.jetbrains" && requested.name == "annotations") {
                useVersion(libs.versions.annotations.get())
            } else if (requested.group == "org.jetbrains.kotlinx" && requested.name.lowercase()
                    .contains("coroutines")
            ) {
                useVersion(libs.versions.kotlinxCoroutinesCore.get())
            }
            /*else {
                println("requested: ${requested.group}:${requested.name}:${requested.version}")
            }*/

        }
    }
}
gradlePlugin {
    plugins {
            register("HelloRustPlugin") {
            id = "com.seiko.plugin.rust"
            version = "1.0.0"
            implementationClass = "com.seiko.plugin.rust.RustKotlinPlugin"
        }
    }
}
