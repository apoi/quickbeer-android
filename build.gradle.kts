import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id(Plugins.Id.Android.Application) version Versions.Plugin.Gradle apply false
    kotlin(Plugins.Id.Kotlin.Android) version Versions.Plugin.Kotlin apply false
    id(Plugins.Id.Room) version Versions.AndroidX.Room apply false
    id(Plugins.Id.Detekt) version Versions.Plugin.Detekt
    id(Plugins.Id.Ktlint) version Versions.Plugin.Ktlint
    id(Plugins.Id.Version) version Versions.Plugin.Version
}

buildscript {
    repositories {
        google()
    }

    dependencies {
        classpath(Libraries.AndroidX.Navigation.SafeArgs)
        classpath(Libraries.Hilt.Gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

subprojects {
    apply {
        plugin(Plugins.Id.Detekt)
        plugin(Plugins.Id.Ktlint)
    }

    ktlint {
        version.set(Versions.Ktlint)
        verbose.set(true)
        android.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    detekt {
        baseline = file("${rootProject.projectDir}/config/detekt/baseline.xml")
        parallel = true
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()
