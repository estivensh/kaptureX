import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()

    gradlePluginPortal()
}

kotlin {
    jvmToolchain(11)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    api(libs.kotlinGradlePlugin)
    api(libs.androidGradlePlugin)
    api(libs.gradle.maven.publish.plugin)
}
