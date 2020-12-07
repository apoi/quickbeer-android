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
import quickbeer.android.domain.beersearch.network.TopBeersFetcher
import quickbeer.android.domain.beersearch.repository.BeerSearchRepository
import quickbeer.android.domain.beersearch.repository.TopBeersRepository
import quickbeer.android.domain.beersearch.store.BeerListStore
import quickbeer.android.domain.beersearch.store.TopBeersStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.idlist.store.IdListRoomCore
import quickbeer.android.domain.recentbeers.RecentBeersStore
import quickbeer.android.feature.beerdetails.BeerDetailsViewModel
import quickbeer.android.feature.recentbeers.RecentBeersViewModel
import quickbeer.android.feature.search.SearchViewModel
import quickbeer.android.feature.topbeers.TopBeersViewModel
import quickbeer.android.network.NetworkConfig
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.adapter.EscapedStringAdapter
import quickbeer.android.network.adapter.ZonedDateTimeAdapter
import quickbeer.android.network.interceptor.AppKeyInterceptor
import quickbeer.android.network.interceptor.LoggingInterceptor
import quickbeer.android.network.result.ResultCallAdapterFactory
import quickbeer.android.util.ToastProvider
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val TEN_MEGABYTES: Long = 10 * 1024 * 1024

val appModule = module {

    single {
        OkHttpClient.Builder()
            .cache(Cache(androidContext().cacheDir, TEN_MEGABYTES))
            .addInterceptor(AppKeyInterceptor())
            .addInterceptor(LoggingInterceptor.create())
            .build()
    }

    single {
        Moshi.Builder()
            .add(ZonedDateTimeAdapter())
            .add(EscapedStringAdapter())
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

    single { ToastProvider(get()) }

    // Cores
    single<StoreCore<String, IdList>>(named<IdList>()) {
        CachingStoreCore(IdListRoomCore(get()), IdList::key)
    }
    single<StoreCore<Int, Beer>>(named<Beer>()) {
        CachingStoreCore(BeerRoomCore(get()), Beer::id, Beer.merger)
    }

    // Stores
    factory { BeerStore(get(named<Beer>())) }
    factory { BeerListStore(get(named<IdList>()), get(named<Beer>())) }
    factory { TopBeersStore(get(named<IdList>()), get(named<Beer>())) }
    factory { RecentBeersStore(get(named<IdList>()), get(named<Beer>())) }

    // Fetchers
    factory { BeerFetcher(get()) }
    factory { TopBeersFetcher(get()) }
    factory { BeerSearchFetcher(get()) }

    // Repositories
    factory { BeerRepository(get(), get()) }
    factory { TopBeersRepository(get(), get()) }
    factory { BeerSearchRepository(get(), get()) }

    viewModel { RecentBeersViewModel(get(), get()) }
    viewModel { TopBeersViewModel(get(), get()) }
    viewModel { (query: String) -> SearchViewModel(query, get(), get()) }
    viewModel { (id: Int) -> BeerDetailsViewModel(id, get()) }
}
