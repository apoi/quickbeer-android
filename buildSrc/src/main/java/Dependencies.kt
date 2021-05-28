object Sdk {
    const val MinSdkVersion = 23
    const val TargetSdkVersion = 30
    const val CompileSdkVersion = 30
}

object Plugins {
    object Id {
        object Kotlin {
            const val Android = "android"
            const val AndroidExtensions = "android.extensions"
            const val Kapt = "kapt"
        }

        object Android {
            const val Application = "com.android.application"
            const val Library = "com.android.library"
        }

        const val Detekt = "io.gitlab.arturbosch.detekt"
        const val Ktlint = "org.jlleitschuh.gradle.ktlint"
        const val Version = "com.github.ben-manes.versions"
        const val SafeArgs = "androidx.navigation.safeargs.kotlin"
    }
}

object Versions {
    object Plugin {
        const val Detekt = "1.17.1"
        const val Gradle = "4.2.1"
        const val Kotlin = "1.5.10"
        const val Ktlint = "10.0.0"
        const val Version = "0.39.0"
    }

    object AndroidX {
        const val AppCompat = "1.3.0"
        const val Camera = "1.0.0-rc04"
        const val ConstraintLayout = "2.0.4"
        const val CoreKt = "1.5.0"
        const val Crypto = "1.0.0"
        const val Fragment = "1.3.4"
        const val Lifecycle = "2.3.1"
        const val Navigation = "2.3.5"
        const val RecyclerView = "1.2.0"
        const val Room = "2.3.0"
        const val SwipeRefreshLayout = "1.1.0"
    }

    object Google {
        const val Material = "1.3.0"
        const val Barcode = "16.1.2"
    }

    object OkHttp {
        const val OkHttp = "4.9.1"
        const val AndroidSupport = "3.13.1"
    }

    object Kotlin {
        const val Kotlin = Plugin.Kotlin
        const val Coroutines = "1.5.0"
    }

    const val Coil = "1.2.1"
    const val CookieJar = "1.0.1"
    const val Koin = "2.2.2"
    const val Ktlint = "0.41.0"
    const val LeakCanary = "2.7"
    const val PhotoView = "2.3.0"
    const val Retrofit = "2.9.0"
    const val ThreeTenABP = "1.3.1"
    const val Timber = "4.7.1"

    object Testing {
        object AndroidX {
            const val Test = "1.3.0"
            const val TestExt = "1.1.2"
        }

        const val EspressoCore = "3.3.0"
        const val JUnit = "4.13.2"
    }
}

object Libraries {
    object AndroidX {
        object Camera {
            const val Core = "androidx.camera:camera-core:${Versions.AndroidX.Camera}"
        }

        object Lifecycle {
            const val Common = "androidx.lifecycle:lifecycle-common-java8:${Versions.AndroidX.Lifecycle}"
            const val LiveData = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.Lifecycle}"
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
        const val ConstraintLayout = "com.android.support.constraint:constraint-layout:${Versions.AndroidX.ConstraintLayout}"
        const val CoreKtx = "androidx.core:core-ktx:${Versions.AndroidX.CoreKt}"
        const val Crypto = "androidx.security:security-crypto:${Versions.AndroidX.Crypto}"
        const val Fragment = "androidx.fragment:fragment:${Versions.AndroidX.Fragment}"
        const val RecyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.RecyclerView}"
        const val SwipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.SwipeRefreshLayout}"
    }

    object Google {
        const val Material = "com.google.android.material:material:${Versions.Google.Material}"
        const val Barcode = "com.google.mlkit:barcode-scanning:${Versions.Google.Barcode}"
    }

    object Koin {
        const val Core = "io.insert-koin:koin-core:${Versions.Koin}"
        const val Android = "io.insert-koin:koin-android:${Versions.Koin}"
        const val Scope = "io.insert-koin:koin-androidx-scope:${Versions.Koin}"
        const val ViewModel = "io.insert-koin:koin-androidx-viewmodel:${Versions.Koin}"
        const val Extension = "io.insert-koin:koin-androidx-ext:${Versions.Koin}"
        const val Test = "io.insert-koin:koin-test:${Versions.Koin}"
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
