package quickbeer.android.next.data;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.data.store.StoreModule;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.injections.ForApplication;
import quickbeer.android.next.network.ServiceDataLayer;
import quickbeer.android.next.network.fetchers.FetcherModule;

@Module(includes = { FetcherModule.class, StoreModule.class })
public final class DataStoreModule {

    @Provides
    public DataLayer.GetUserSettings provideGetUserSettings(DataLayer dataLayer) {
        return dataLayer::getUserSettings;
    }

    @Provides
    public DataLayer.SetUserSettings provideSetUserSettings(DataLayer dataLayer) {
        return dataLayer::setUserSettings;
    }

    @Provides
    public DataLayer.GetBeer provideGetBeer(DataLayer dataLayer) {
        return dataLayer::getBeer;
    }

    @Provides
    public DataLayer.GetBeerSearchQueries provideGetBeerSearchQueries(DataLayer dataLayer) {
        return dataLayer::getBeerSearchQueries;
    }

    @Provides
    public DataLayer.GetBeerSearch provideGetBeerSearch(DataLayer dataLayer) {
        return dataLayer::getBeerSearch;
    }

    @Provides
    public DataLayer.GetTopBeers provideGetTopBeers(DataLayer dataLayer) {
        return dataLayer::getTopBeers;
    }

    @Provides
    public DataLayer.GetReviewsForBeer provideGetReviewsForBeer(DataLayer dataLayer) {
        return dataLayer::getReviews;
    }

    @Provides
    @Singleton
    public DataLayer provideApplicationDataLayer(@ForApplication Context context,
                                                 UserSettingsStore userSettingsStore,
                                                 NetworkRequestStatusStore networkRequestStatusStore,
                                                 BeerStore beerStore,
                                                 BeerSearchStore beerSearchStore,
                                                 ReviewStore reviewStore,
                                                 ReviewListStore reviewListStore) {
        return new DataLayer(context,
                userSettingsStore,
                networkRequestStatusStore,
                beerStore,
                beerSearchStore,
                reviewStore,
                reviewListStore);
    }

    @Provides
    @Singleton
    public ServiceDataLayer provideServiceDataLayer(@Named("beerFetcher") Fetcher beerFetcher,
                                                    @Named("beerSearchFetcher") Fetcher beerSearchFetcher,
                                                    @Named("topBeersFetcher") Fetcher topBeersFetcher,
                                                    NetworkRequestStatusStore networkRequestStatusStore,
                                                    BeerStore beerStore,
                                                    BeerSearchStore beerSearchStore,
                                                    ReviewStore reviewStore,
                                                    ReviewListStore reviewListStore) {
        return new ServiceDataLayer(beerFetcher,
                beerSearchFetcher,
                topBeersFetcher,
                networkRequestStatusStore,
                beerStore,
                beerSearchStore,
                reviewStore,
                reviewListStore);
    }
}
