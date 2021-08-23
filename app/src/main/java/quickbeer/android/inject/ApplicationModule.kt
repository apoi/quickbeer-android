package quickbeer.android.inject

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import quickbeer.android.data.room.DATABASE_NAME
import quickbeer.android.data.room.Database
import quickbeer.android.util.LegacyPreferences
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.ToastProvider

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(context, Database::class.java, DATABASE_NAME)
            .build()
    }

    @Provides
    @Reusable
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProvider(context)
    }

    @Provides
    @Singleton
    fun provideToastProvider(@ApplicationContext context: Context): ToastProvider {
        return ToastProvider(context)
    }

    @Provides
    @Reusable
    fun providePreferences(@ApplicationContext context: Context): LegacyPreferences {
        return LegacyPreferences(context)
    }
}
