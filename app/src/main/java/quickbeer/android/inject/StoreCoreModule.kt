package quickbeer.android.inject

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.idlist.store.IdListRoomCore
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.store.ReviewRoomCore
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.store.StyleRoomCore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdListPersistedCore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IdListMemoryCore

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
        return CachingStoreCore(roomCore, Style::id, Style.merger)
    }

    @Provides
    @Singleton
    fun provideCountryCore(): StoreCore<Int, Country> {
        return MemoryStoreCore()
    }

    @Provides
    @Singleton
    fun provideReviewCore(roomCore: ReviewRoomCore): StoreCore<Int, Review> {
        return CachingStoreCore(roomCore, Review::id, Review.merger)
    }
}
