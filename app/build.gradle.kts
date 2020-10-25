plugins {
    id(Plugins.Id.Android.Application)
    kotlin(Plugins.Id.Kotlin.Android)
    kotlin(Plugins.Id.Kotlin.AndroidExtensions)
    kotlin(Plugins.Id.Kotlin.Kapt)
    id(Plugins.Id.SafeArgs)
}

android {
    compileSdkVersion(Sdk.CompileSdkVersion)

    defaultConfig {
        minSdkVersion(Sdk.MinSdkVersion)
        targetSdkVersion(Sdk.TargetSdkVersion)

        applicationId = App.AppId
        versionCode = appBuildVersion()
        versionName = appVersionName()

        val appKey = System.getenv("RATEBEER_KEY")
        buildConfigField("String", "APP_KEY", "\"$appKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    androidExtensions {
        features = setOf("parcelize")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
    }

    lintOptions {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
}

dependencies {
    implementation(Libraries.AndroidX.AppCompat)
    implementation(Libraries.AndroidX.ConstraintLayout)
    implementation(Libraries.AndroidX.CoreKtx)
    implementation(Libraries.AndroidX.Lifecycle.Common)
    implementation(Libraries.AndroidX.Lifecycle.LiveData)
    implementation(Libraries.AndroidX.Lifecycle.ViewModel)
    implementation(Libraries.AndroidX.Navigation.Fragment)
    implementation(Libraries.AndroidX.Navigation.Ui)
    implementation(Libraries.AndroidX.RecyclerView)
    implementation(Libraries.AndroidX.Room.Runtime)
    kapt(Libraries.AndroidX.Room.Compiler)
    implementation(Libraries.AndroidX.Room.Extensions)
    implementation(Libraries.AndroidX.SwipeRefreshLayout)

    implementation(Libraries.Google.Material)

    implementation(Libraries.Koin.Android)
    implementation(Libraries.Koin.Scope)
    implementation(Libraries.Koin.ViewModel)

    implementation(Libraries.Kotlin.Coroutines.Core)
    implementation(Libraries.Kotlin.Coroutines.Android)

    implementation(Libraries.OkHttp.OkHttp)
    implementation(Libraries.OkHttp.AndroidSupport)
    implementation(Libraries.OkHttp.LoggingInterceptor)

    implementation(Libraries.Picasso.Picasso)
    implementation(Libraries.Picasso.OkHttpDownloader)

    implementation(Libraries.Retrofit.Retrofit)
    implementation(Libraries.Retrofit.MoshiConverter)

    implementation(Libraries.ThreeTenABP)
    implementation(Libraries.Timber)

    // Test
    testImplementation(TestingLibraries.JUnit)

    // Android test
    androidTestImplementation(AndroidTestingLibraries.AndroidX.TestExtJUnit)
    androidTestImplementation(AndroidTestingLibraries.AndroidX.TestRules)
    androidTestImplementation(AndroidTestingLibraries.EspressoCore)
}
