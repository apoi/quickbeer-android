package quickbeer.android.inject

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import quickbeer.android.data.repository.repository.ItemList
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.data.store.core.MemoryStoreCore
import quickbeer.android.domain.beer.store.BeerRoomCore
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.brewer.store.BrewerRoomCore
import quickbeer.android.domain.brewer.store.BrewerStoreCore
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.feed.FeedDataItem
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.idlist.store.IdListRoomCore
import quickbeer.android.domain.preferences.core.IntPreferenceStoreCore
import quickbeer.android.domain.preferences.core.StringPreferenceStoreCore
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.store.RatingRoomCore
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.store.StyleRoomCore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdListPersistedCore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdListMemoryCore

private val Context.dataStore by preferencesDataStore(
    name = "quickbeer_datastore",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration({
                PreferenceManager.getDefaultSharedPreferences(context)
            })
        )
    }
)

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    @IdListPersistedCore
    fun provideIdListPersistedCore(roomCore: IdListRoomCore): StoreCore<String, IdList> {
        return CachingStoreCore(roomCore, IdList::key)
    }

    @Provides
    @Singleton
    @IdListMemoryCore
    fun provideIdListMemoryCore(): StoreCore<String, IdList> {
        return MemoryStoreCore(ItemList.join())
    }

    @Provides
    @Singleton
    fun provideBeerStoreCore(roomCore: BeerRoomCore): BeerStoreCore {
        return BeerStoreCore(roomCore)
    }

    @Provides
    @Singleton
    fun provideBrewerStoreCore(roomCore: BrewerRoomCore): BrewerStoreCore {
        return BrewerStoreCore(roomCore)
    }

    @Provides
    @Singleton
    fun provideStyleStoreCore(roomCore: StyleRoomCore): StoreCore<Int, Style> {
        return CachingStoreCore(roomCore, Style::id)
    }

    @Provides
    @Singleton
    fun provideCountryCore(): StoreCore<Int, Country> {
        return MemoryStoreCore(StoreCore.takeNew())
    }

    @Provides
    @Singleton
    fun provideRatingCore(roomCore: RatingRoomCore): StoreCore<Int, Rating> {
        return CachingStoreCore(roomCore, Rating::id)
    }

    @Provides
    @Singleton
    fun provideFeedItemCore(): StoreCore<Int, FeedDataItem> {
        return MemoryStoreCore(FeedDataItem.merger)
    }

    @Provides
    @Singleton
    fun provideStringPrefCore(@ApplicationContext context: Context): StringPreferenceStoreCore {
        return StringPreferenceStoreCore(context.dataStore)
    }

    @Provides
    @Singleton
    fun provideIntPrefCore(@ApplicationContext context: Context): IntPreferenceStoreCore {
        return IntPreferenceStoreCore(context.dataStore)
    }
}
