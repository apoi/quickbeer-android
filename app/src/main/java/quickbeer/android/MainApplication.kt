package quickbeer.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import quickbeer.android.inject.appModule
import timber.log.Timber

class MainApplication : Application(), ImageLoaderFactory {

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

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .componentRegistry { add(SvgDecoder(applicationContext)) }
            .build()
    }
}
