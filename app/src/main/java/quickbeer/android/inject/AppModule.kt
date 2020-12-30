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
import quickbeer.android.data.store.core.MemoryStoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerFetcher
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beer.store.BeerRoomCore
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.network.BeersInCountryFetcher
import quickbeer.android.domain.beerlist.network.BeersInStyleFetcher
import quickbeer.android.domain.beerlist.network.BrewersBeersFetcher
import quickbeer.android.domain.beerlist.network.TopBeersFetcher
import quickbeer.android.domain.beerlist.repository.BeerSearchRepository
import quickbeer.android.domain.beerlist.repository.BeersInCountryRepository
import quickbeer.android.domain.beerlist.repository.BeersInStyleRepository
import quickbeer.android.domain.beerlist.repository.BrewersBeersRepository
import quickbeer.android.domain.beerlist.repository.TopBeersRepository
import quickbeer.android.domain.beerlist.store.BeerSearchStore
import quickbeer.android.domain.beerlist.store.BeersInCountryStore
import quickbeer.android.domain.beerlist.store.BeersInStyleStore
import quickbeer.android.domain.beerlist.store.BrewersBeersStore
import quickbeer.android.domain.beerlist.store.RecentBeersStore
import quickbeer.android.domain.beerlist.store.TopBeersStore
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerFetcher
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.brewer.store.BrewerRoomCore
import quickbeer.android.domain.brewer.store.BrewerStore
import quickbeer.android.domain.brewerlist.network.BrewerSearchFetcher
import quickbeer.android.domain.brewerlist.repository.BrewerSearchRepository
import quickbeer.android.domain.brewerlist.store.BrewerSearchStore
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.country.store.CountryStore
import quickbeer.android.domain.countrylist.network.CountryListFetcher
import quickbeer.android.domain.countrylist.repository.CountryListRepository
import quickbeer.android.domain.countrylist.store.CountryListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.idlist.store.IdListRoomCore
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.domain.style.store.StyleRoomCore
import quickbeer.android.domain.style.store.StyleStore
import quickbeer.android.domain.stylelist.network.StyleListFetcher
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.domain.stylelist.store.StyleListStore
import quickbeer.android.feature.beerdetails.BeerDetailsViewModel
import quickbeer.android.feature.brewerdetails.BrewerDetailsViewModel
import quickbeer.android.feature.countrydetails.CountryDetailsViewModel
import quickbeer.android.feature.recentbeers.RecentBeersViewModel
import quickbeer.android.feature.search.SearchViewModel
import quickbeer.android.feature.styledetails.StyleDetailsViewModel
import quickbeer.android.feature.styles.StylesViewModel
import quickbeer.android.feature.topbeers.TopBeersViewModel
import quickbeer.android.network.NetworkConfig
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.adapter.EscapedStringAdapter
import quickbeer.android.network.adapter.ZonedDateTimeAdapter
import quickbeer.android.network.interceptor.AppKeyInterceptor
import quickbeer.android.network.interceptor.LoggingInterceptor
import quickbeer.android.network.result.ResultCallAdapterFactory
import quickbeer.android.util.ResourceProvider
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

    factory { ResourceProvider(androidContext()) }

    single { ToastProvider(get()) }

    // Cores
    single<StoreCore<String, IdList>>(named<IdList>()) {
        CachingStoreCore(IdListRoomCore(get()), IdList::key)
    }
    single<StoreCore<Int, Beer>>(named<Beer>()) {
        CachingStoreCore(BeerRoomCore(get()), Beer::id, Beer.merger)
    }
    single<StoreCore<Int, Brewer>>(named<Brewer>()) {
        CachingStoreCore(BrewerRoomCore(get()), Brewer::id, Brewer.merger)
    }
    single<StoreCore<Int, Style>>(named<Style>()) {
        CachingStoreCore(StyleRoomCore(get()), Style::id, Style.merger)
    }
    single<StoreCore<Int, Country>>(named<Country>()) {
        MemoryStoreCore(Country.merger)
    }

    // Stores
    factory { BeerStore(get(named<Beer>())) }
    factory { BeerSearchStore(get(named<IdList>()), get(named<Beer>())) }
    factory { TopBeersStore(get(named<IdList>()), get(named<Beer>())) }
    factory { BrewersBeersStore(get(named<IdList>()), get(named<Beer>())) }
    factory { BeersInStyleStore(get(named<IdList>()), get(named<Beer>())) }
    factory { BeersInCountryStore(get(named<IdList>()), get(named<Beer>())) }
    factory { RecentBeersStore(get(named<IdList>()), get(named<Beer>())) }
    factory { BrewerStore(get(named<Brewer>())) }
    factory { BrewerSearchStore(get(named<IdList>()), get(named<Brewer>())) }
    factory { StyleStore(get(named<Style>())) }
    factory { StyleListStore(get(named<IdList>()), get(named<Style>())) }
    factory { CountryStore(get(named<Country>())) }
    factory { CountryListStore(get(named<IdList>()), get(named<Country>())) }

    // Fetchers
    single { BeerFetcher(get()) }
    single { BeerSearchFetcher(get()) }
    single { TopBeersFetcher(get()) }
    single { BrewersBeersFetcher(get()) }
    single { BeersInStyleFetcher(get()) }
    single { BeersInCountryFetcher(get()) }
    single { BrewerFetcher(get()) }
    single { BrewerSearchFetcher(get()) }
    single { StyleListFetcher(get()) }
    single { CountryListFetcher(get(), get()) }

    // Repositories
    factory { BeerRepository(get(), get()) }
    factory { BeerSearchRepository(get(), get()) }
    factory { TopBeersRepository(get(), get()) }
    factory { BrewersBeersRepository(get(), get()) }
    factory { BeersInStyleRepository(get(), get()) }
    factory { BeersInCountryRepository(get(), get()) }
    factory { BrewerRepository(get(), get()) }
    factory { BrewerSearchRepository(get(), get()) }
    factory { StyleRepository(get(), get()) }
    factory { StyleListRepository(get(), get()) }
    factory { CountryRepository(get(), get()) }
    factory { CountryListRepository(get(), get()) }

    // ViewModels
    viewModel { RecentBeersViewModel(get(), get()) }
    viewModel { TopBeersViewModel(get(), get()) }
    viewModel { StylesViewModel(get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { (id: Int) -> BeerDetailsViewModel(id, get(), get(), get(), get(), get()) }
    viewModel { (id: Int) -> BrewerDetailsViewModel(id, get(), get(), get(), get()) }
    viewModel { (id: Int) -> StyleDetailsViewModel(id, get(), get(), get()) }
    viewModel { (id: Int) -> CountryDetailsViewModel(id, get(), get(), get()) }
}
