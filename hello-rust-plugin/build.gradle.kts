plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.2")
    implementation(kotlin("gradle-plugin", version = "1.7.10"))
}

gradlePlugin {
    plugins {
        register("HelloRustAction") {
            id = "com.seiko.transform.action.rust"
            implementationClass = "com.seiko.transform.action.rust.RustKotlinPlugin"
        }
    }
}
