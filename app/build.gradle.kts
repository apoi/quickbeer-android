plugins {
    id(Plugins.Id.Android.Application)
    kotlin(Plugins.Id.Kotlin.Android)
    id(Plugins.Id.Parcelize)
    id(Plugins.Id.SafeArgs)
    id(Plugins.Id.Hilt)
    id(Plugins.Id.Room)
    id(Plugins.Id.Ksp)
}

android {
    compileSdk = Sdk.CompileSdkVersion
    namespace = "quickbeer.android"

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.AndroidX.Compose.Compiler
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
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

    implementation(Libraries.AndroidX.Compose.Material2)
    implementation(Libraries.AndroidX.Compose.Ui)

    implementation(Libraries.AndroidX.Lifecycle.Common)
    implementation(Libraries.AndroidX.Lifecycle.Runtime)
    implementation(Libraries.AndroidX.Lifecycle.ViewModel)

    implementation(Libraries.AndroidX.Navigation.Fragment)
    implementation(Libraries.AndroidX.Navigation.Ui)

    ksp(Libraries.AndroidX.Room.Compiler)
    implementation(Libraries.AndroidX.Room.Runtime)
    implementation(Libraries.AndroidX.Room.Extensions)

    implementation(Libraries.Google.Material)
    implementation(Libraries.Google.Barcode)

    implementation(Libraries.Hilt.Android)
    ksp(Libraries.Hilt.Compiler)

    implementation(Libraries.OkHttp.OkHttp)
    implementation(Libraries.OkHttp.AndroidSupport)
    implementation(Libraries.OkHttp.LoggingInterceptor)

    implementation(Libraries.Coil.Coil)
    implementation(Libraries.Coil.Svg)

    implementation(Libraries.Retrofit.Retrofit)
    implementation(Libraries.Retrofit.MoshiConverter)
    ksp(Libraries.MoshiCodegen)

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
