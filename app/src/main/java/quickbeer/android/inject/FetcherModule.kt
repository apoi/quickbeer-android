package quickbeer.android.inject

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import quickbeer.android.domain.beer.network.BeerFetcher
import quickbeer.android.domain.beerlist.network.BarcodeSearchFetcher
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.network.BeersInCountryFetcher
import quickbeer.android.domain.beerlist.network.BeersInStyleFetcher
import quickbeer.android.domain.beerlist.network.BrewersBeersFetcher
import quickbeer.android.domain.beerlist.network.TopBeersFetcher
import quickbeer.android.domain.brewer.network.BrewerFetcher
import quickbeer.android.domain.brewerlist.network.BrewerSearchFetcher
import quickbeer.android.domain.countrylist.network.CountryListFetcher
import quickbeer.android.domain.reviewlist.network.BeerReviewsFetcher
import quickbeer.android.domain.reviewlist.network.BeerReviewsPageFetcher
import quickbeer.android.domain.stylelist.network.StyleListFetcher
import quickbeer.android.domain.user.network.RateCountFetcher
import quickbeer.android.network.RateBeerApi
import quickbeer.android.util.ResourceProvider

@Module
@InstallIn(SingletonComponent::class)
object FetcherModule {

    @Provides
    @Singleton
    fun provideBeerFetcher(api: RateBeerApi): BeerFetcher {
        return BeerFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBeerSearchFetcher(api: RateBeerApi): BeerSearchFetcher {
        return BeerSearchFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBarcodeSearchFetcher(api: RateBeerApi): BarcodeSearchFetcher {
        return BarcodeSearchFetcher(api)
    }

    @Provides
    @Singleton
    fun provideTopBeersFetcher(api: RateBeerApi): TopBeersFetcher {
        return TopBeersFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBrewersBeersFetcher(api: RateBeerApi): BrewersBeersFetcher {
        return BrewersBeersFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBeersInStyleFetcher(api: RateBeerApi): BeersInStyleFetcher {
        return BeersInStyleFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBeersInCountryFetcher(api: RateBeerApi): BeersInCountryFetcher {
        return BeersInCountryFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBrewerFetcher(api: RateBeerApi): BrewerFetcher {
        return BrewerFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBrewerSearchFetcher(api: RateBeerApi): BrewerSearchFetcher {
        return BrewerSearchFetcher(api)
    }

    @Provides
    @Singleton
    fun provideStyleListFetcher(api: RateBeerApi): StyleListFetcher {
        return StyleListFetcher(api)
    }

    @Provides
    @Singleton
    fun provideCountryListFetcher(
        resourceProvider: ResourceProvider,
        moshi: Moshi
    ): CountryListFetcher {
        return CountryListFetcher(resourceProvider, moshi)
    }

    @Provides
    @Singleton
    fun provideBeerReviewsFetcher(api: RateBeerApi): BeerReviewsFetcher {
        return BeerReviewsFetcher(api)
    }

    @Provides
    @Singleton
    fun provideBeerReviewsPageFetcher(api: RateBeerApi): BeerReviewsPageFetcher {
        return BeerReviewsPageFetcher(api)
    }

    @Provides
    @Singleton
    fun provideRateCountFetcher(api: RateBeerApi): RateCountFetcher {
        return RateCountFetcher(api)
    }
}
