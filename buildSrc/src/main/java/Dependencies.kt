object Sdk {
    const val MinSdkVersion = 23
    const val TargetSdkVersion = 34
    const val CompileSdkVersion = 34
}

object Plugins {
    object Id {
        object Kotlin {
            const val Android = "android"
        }

        object Android {
            const val Application = "com.android.application"
            const val Library = "com.android.library"
        }

        const val Detekt = "io.gitlab.arturbosch.detekt"
        const val Hilt = "dagger.hilt.android.plugin"
        const val Ksp = "com.google.devtools.ksp"
        const val Ktlint = "org.jlleitschuh.gradle.ktlint"
        const val Parcelize = "kotlin-parcelize"
        const val Version = "com.github.ben-manes.versions"
        const val SafeArgs = "androidx.navigation.safeargs.kotlin"
        const val Room = "androidx.room"
    }
}

object Versions {
    object Plugin {
        const val Detekt = "1.23.6"
        const val Gradle = "8.5.1"
        const val Kotlin = "1.9.24"
        const val Ksp = "1.9.24-1.0.20"
        const val Ktlint = "12.1.0"
        const val Version = "0.51.0"
    }

    object AndroidX {
        object Compose {
            const val Compiler = "1.5.14"
            const val Material2 = "1.6.8"
            const val Runtime = "1.6.8"
            const val Ui = "1.6.8"
        }

        const val AppCompat = "1.7.0"
        const val ConstraintLayout = "2.1.4"
        const val CoreKt = "1.13.1"
        const val DataStore = "1.1.1"
        const val Fragment = "1.8.1"
        const val Lifecycle = "2.8.3"
        const val Navigation = "2.7.7"
        const val Preferences = "1.2.1"
        const val RecyclerView = "1.3.2"
        const val Room = "2.6.1"
        const val SwipeRefreshLayout = "1.1.0"
    }

    object Google {
        const val Material = "1.12.0"
        const val Barcode = "17.2.0"
    }

    object OkHttp {
        const val OkHttp = "4.12.0"
        const val AndroidSupport = "3.13.1"
    }

    object Kotlin {
        const val Kotlin = Plugin.Kotlin
        const val Coroutines = "1.8.1"
    }

    const val Coil = "1.4.0"
    const val CookieJar = "1.0.1"
    const val Hilt = "2.51.1"
    const val Ktlint = "1.1.1"
    const val LeakCanary = "2.14"
    const val Moshi = "1.15.1"
    const val PhotoView = "2.3.0"
    const val Retrofit = "2.11.0"
    const val ThreeTenABP = "1.4.7"
    const val Timber = "5.0.1"

    object Testing {
        object AndroidX {
            const val Test = "1.6.1"
            const val TestExt = "1.2.1"
        }

        const val EspressoCore = "3.6.1"
        const val JUnit = "4.13.2"
    }
}

object Libraries {
    object AndroidX {
        object Compose {
            const val Material2 = "androidx.compose.material:material:${Versions.AndroidX.Compose.Material2}"
            const val Ui = "androidx.compose.ui:ui:${Versions.AndroidX.Compose.Ui}"
        }

        object Lifecycle {
            const val Common = "androidx.lifecycle:lifecycle-common-java8:${Versions.AndroidX.Lifecycle}"
            const val Runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.AndroidX.Lifecycle}"
            const val ViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.Lifecycle}"
        }

        object Navigation {
            const val Fragment = "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.Navigation}"
            const val SafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.AndroidX.Navigation}"
            const val Ui = "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.Navigation}"
        }

        object Room {
            const val Runtime = "androidx.room:room-runtime:${Versions.AndroidX.Room}"
            const val Compiler = "androidx.room:room-compiler:${Versions.AndroidX.Room}"
            const val Extensions = "androidx.room:room-ktx:${Versions.AndroidX.Room}"
        }

        const val AppCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.AppCompat}"
        const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.ConstraintLayout}"
        const val CoreKtx = "androidx.core:core-ktx:${Versions.AndroidX.CoreKt}"
        const val DataStore = "androidx.datastore:datastore-preferences:${Versions.AndroidX.DataStore}"
        const val Fragment = "androidx.fragment:fragment:${Versions.AndroidX.Fragment}"
        const val Preferences = "androidx.preference:preference:${Versions.AndroidX.Preferences}"
        const val RecyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.RecyclerView}"
        const val SwipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.SwipeRefreshLayout}"
    }

    object Google {
        const val Material = "com.google.android.material:material:${Versions.Google.Material}"
        const val Barcode = "com.google.mlkit:barcode-scanning:${Versions.Google.Barcode}"
    }

    object Hilt {
        const val Android = "com.google.dagger:hilt-android:${Versions.Hilt}"
        const val Compiler = "com.google.dagger:hilt-android-compiler:${Versions.Hilt}"
        const val Gradle = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Hilt}"
    }

    object Kotlin {
        object Coroutines {
            const val Core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.Coroutines}"
            const val Android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.Coroutines}"
        }
    }

    object OkHttp {
        const val OkHttp = "com.squareup.okhttp3:okhttp:${Versions.OkHttp.OkHttp}"
        const val AndroidSupport = "com.squareup.okhttp3:okhttp-android-support:${Versions.OkHttp.AndroidSupport}"
        const val LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.OkHttp.OkHttp}"
    }

    object Retrofit {
        const val Retrofit = "com.squareup.retrofit2:retrofit:${Versions.Retrofit}"
        const val MoshiConverter = "com.squareup.retrofit2:converter-moshi:${Versions.Retrofit}"
    }

    object Coil {
        const val Coil = "io.coil-kt:coil:${Versions.Coil}"
        const val Svg = "io.coil-kt:coil-svg:${Versions.Coil}"
    }

    const val CookieJar = "com.github.franmontiel:PersistentCookieJar:${Versions.CookieJar}"
    const val LeakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.LeakCanary}"
    const val MoshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.Moshi}"
    const val PhotoView = "com.github.chrisbanes:PhotoView:${Versions.PhotoView}"
    const val ThreeTenABP = "com.jakewharton.threetenabp:threetenabp:${Versions.ThreeTenABP}"
    const val Timber = "com.jakewharton.timber:timber:${Versions.Timber}"
}

object TestingLibraries {
    const val JUnit = "junit:junit:${Versions.Testing.JUnit}"
}

object AndroidTestingLibraries {
    object AndroidX {
        const val TestRules = "androidx.test:rules:${Versions.Testing.AndroidX.Test}"
        const val TestRunner = "androidx.test:runner:${Versions.Testing.AndroidX.Test}"
        const val TestExtJUnit = "androidx.test.ext:junit:${Versions.Testing.AndroidX.TestExt}"
    }

    const val EspressoCore = "androidx.test.espresso:espresso-core:${Versions.Testing.EspressoCore}"
}
