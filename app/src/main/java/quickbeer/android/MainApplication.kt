package quickbeer.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import quickbeer.android.util.migration.ApplicationMigration
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application(), ImageLoaderFactory {

    @Inject
    lateinit var migration: ApplicationMigration

    override fun onCreate() {
        super.onCreate()

        initThreeTenAbp()
        initTimber()
        migrate()
    }

    private fun initThreeTenAbp() {
        AndroidThreeTen.init(this)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun migrate() {
        migration.migrate()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .componentRegistry { add(SvgDecoder(applicationContext)) }
            .build()
    }
}
