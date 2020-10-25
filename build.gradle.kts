import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id(Plugins.Id.Android.Application) version Versions.Plugin.Gradle apply false
    kotlin(Plugins.Id.Kotlin.Android) version Versions.Plugin.Kotlin apply false
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
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
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
        config = rootProject.files("config/detekt/detekt.yml")
        baseline = rootProject.file("config/detekt/baseline.xml")
        parallel = true
        reports {
            html {
                enabled = true
                destination = file("build/reports/detekt.html")
            }
        }
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
