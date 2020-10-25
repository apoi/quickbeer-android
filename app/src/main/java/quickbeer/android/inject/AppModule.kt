package quickbeer.android.inject

import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import quickbeer.android.data.room.DATABASE_NAME
import quickbeer.android.data.room.Database
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.core.CachingStoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerFetcher
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beer.store.BeerRoomCore
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.beersearch.network.BeerSearchFetcher
import quickbeer.android.domain.beersearch.repository.BeerSearchRepository
import quickbeer.android.domain.beersearch.store.BeerSearchStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.idlist.store.IdListRoomCore
import quickbeer.android.domain.recentbeers.RecentBeersStore
import quickbeer.android.feature.beers.RecentBeersViewModel
import quickbeer.android.network.NetworkConfig
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.adapter.ZonedDateTimeAdapter
import quickbeer.android.network.interceptor.AppKeyInterceptor
import quickbeer.android.network.result.ResultCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TEN_MEGABYTES: Long = 10 * 1024 * 1024

val appModule = module {

    single {
        OkHttpClient.Builder()
            .cache(Cache(androidContext().cacheDir, TEN_MEGABYTES))
            .addInterceptor(AppKeyInterceptor())
            .build()
    }

    single {
        Moshi.Builder()
            .add(ZonedDateTimeAdapter())
            .build()
    }

    single {
        Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .baseUrl(NetworkConfig.API_ENDPOINT)
            .client(get())
            .build()
    }

    single {
        get<Retrofit>().create(RateBeerApi::class.java)
    }

    single {
        Picasso.Builder(get())
            .downloader(OkHttp3Downloader(get<OkHttpClient>()))
            .build()
    }

    single {
        Room.databaseBuilder(get(), Database::class.java, DATABASE_NAME)
            .build()
    }

    // IdList core is used for storing lists of items
    single<StoreCore<String, IdList>>(named<IdList>()) {
        CachingStoreCore(IdListRoomCore(get()), IdList::key)
    }

    // Stores
    single<StoreCore<Int, Beer>>(named<Beer>()) {
        CachingStoreCore(BeerRoomCore(get()), Beer::id, Beer.merger)
    }
    factory { BeerStore(get(named<Beer>())) }
    factory { BeerSearchStore(get(), get()) }
    factory { RecentBeersStore(get(named<IdList>()), get(named<Beer>())) }

    // Fetchers
    factory { BeerFetcher(get()) }
    factory { BeerSearchFetcher(get()) }

    // Repositories
    factory { BeerRepository(get(), get()) }
    factory { BeerSearchRepository(get(), get()) }

    viewModel { RecentBeersViewModel(get(), get()) }
}
