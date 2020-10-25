package quickbeer.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import quickbeer.android.inject.appModule
import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        initTimber()

        Timber.d("App running!")
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
