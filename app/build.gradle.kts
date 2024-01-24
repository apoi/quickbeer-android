plugins {
    id(Plugins.Id.Android.Application)
    kotlin(Plugins.Id.Kotlin.Android)
    kotlin(Plugins.Id.Kotlin.AndroidExtensions)
    kotlin(Plugins.Id.Kotlin.Kapt)
    id(Plugins.Id.SafeArgs)
    id(Plugins.Id.Hilt)
}

android {
    compileSdk = Sdk.CompileSdkVersion

    defaultConfig {
        minSdk = Sdk.MinSdkVersion
        targetSdk = Sdk.TargetSdkVersion

        applicationId = App.AppId
        versionCode = appVersionCode()
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

    lint {
        isWarningsAsErrors = true
        isAbortOnError = true
    }
}

dependencies {
    implementation(Libraries.Kotlin.Coroutines.Core)
    implementation(Libraries.Kotlin.Coroutines.Android)

    implementation(Libraries.AndroidX.AppCompat)
    implementation(Libraries.AndroidX.ConstraintLayout)
    implementation(Libraries.AndroidX.CoreKtx)
    implementation(Libraries.AndroidX.DataStore)
    implementation(Libraries.AndroidX.Fragment)
    implementation(Libraries.AndroidX.Preferences)
    implementation(Libraries.AndroidX.RecyclerView)

    implementation(Libraries.AndroidX.Lifecycle.Common)
    implementation(Libraries.AndroidX.Lifecycle.Runtime)
    implementation(Libraries.AndroidX.Lifecycle.ViewModel)

    implementation(Libraries.AndroidX.Navigation.Fragment)
    implementation(Libraries.AndroidX.Navigation.Ui)

    kapt(Libraries.AndroidX.Room.Compiler)
    implementation(Libraries.AndroidX.Room.Runtime)
    implementation(Libraries.AndroidX.Room.Extensions)

    implementation(Libraries.Google.Material)
    implementation(Libraries.Google.Barcode)

    implementation(Libraries.Hilt.Android)
    kapt(Libraries.Hilt.Compiler)

    implementation(Libraries.OkHttp.OkHttp)
    implementation(Libraries.OkHttp.AndroidSupport)
    implementation(Libraries.OkHttp.LoggingInterceptor)

    implementation(Libraries.Coil.Coil)
    implementation(Libraries.Coil.Svg)

    implementation(Libraries.Retrofit.Retrofit)
    implementation(Libraries.Retrofit.MoshiConverter)

    implementation(Libraries.PhotoView)
    implementation(Libraries.ThreeTenABP)
    implementation(Libraries.Timber)
    implementation(Libraries.CookieJar)

    // Debug
    debugImplementation(Libraries.LeakCanary)

    // Test
    testImplementation(TestingLibraries.JUnit)

    // Android test
    androidTestImplementation(AndroidTestingLibraries.AndroidX.TestExtJUnit)
    androidTestImplementation(AndroidTestingLibraries.AndroidX.TestRules)
    androidTestImplementation(AndroidTestingLibraries.EspressoCore)
}
