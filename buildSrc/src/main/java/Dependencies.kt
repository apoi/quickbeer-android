object Sdk {
    const val MinSdkVersion = 23
    const val TargetSdkVersion = 29
    const val CompileSdkVersion = 29
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
        const val Detekt = "1.14.1"
        const val Gradle = "4.1.0"
        const val Kotlin = "1.4.10"
        const val Ktlint = "9.4.1"
        const val Version = "0.33.0"
    }

    object AndroidX {
        const val AppCompat = "1.2.0"
        const val ConstraintLayout = "2.0.2"
        const val CoreKt = "1.3.2"
        const val Lifecycle = "2.2.0"
        const val Navigation = "2.3.1"
        const val RecyclerView = "1.1.0"
        const val Room = "2.2.5"
        const val SwipeRefreshLayout = "1.1.0"
    }

    object OkHttp {
        const val OkHttp = "4.9.0"
        const val AndroidSupport = "3.13.1"
    }

    object Picasso {
        const val Picasso = "2.71828"
        const val OkHttpDownloader = "1.1.0"
    }

    object Kotlin {
        const val Kotlin = Plugin.Kotlin
        const val Coroutines = "1.3.9"
    }

    const val Koin = "2.1.6"
    const val Ktlint = "0.39.0"
    const val Material = "1.2.1"
    const val Retrofit = "2.9.0"
    const val ThreeTenABP = "1.2.4"
    const val Timber = "4.7.1"

    object Testing {
        object AndroidX {
            const val Test = "1.3.0"
            const val TestExt = "1.1.2"
        }

        const val EspressoCore = "3.3.0"
        const val JUnit = "4.13.1"
    }
}

object Libraries {
    object AndroidX {
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
        const val RecyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.RecyclerView}"
        const val SwipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.SwipeRefreshLayout}"
    }

    object Google {
        const val Material = "com.google.android.material:material:${Versions.Material}"
    }

    object Koin {
        const val Core = "org.koin:koin-core:${Versions.Koin}"
        const val Android = "org.koin:koin-android:${Versions.Koin}"
        const val Scope = "org.koin:koin-androidx-scope:${Versions.Koin}"
        const val ViewModel = "org.koin:koin-androidx-viewmodel:${Versions.Koin}"
        const val Extension = "org.koin:koin-androidx-ext:${Versions.Koin}"
        const val Test = "org.koin:koin-test:${Versions.Koin}"
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

    object Picasso {
        const val Picasso = "com.squareup.picasso:picasso:${Versions.Picasso.Picasso}"
        const val OkHttpDownloader = "com.jakewharton.picasso:picasso2-okhttp3-downloader:${Versions.Picasso.OkHttpDownloader}"
    }

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
