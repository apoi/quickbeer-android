package quickbeer.android.next.network.fetchers;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reark.reark.network.fetchers.Fetcher;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.NetworkModule;

@Module(includes = NetworkModule.class)
public final class FetcherModule {
    @Provides
    @Named("beerFetcher")
    public Fetcher provideBeerFetcher(NetworkApi networkApi,
                                            NetworkRequestStatusStore networkRequestStatusStore,
                                            BeerStore beerStore) {
        return new BeerFetcher(networkApi,
                networkRequestStatusStore::put,
                beerStore);
    }

    @Provides
    @Named("beerSearchFetcher")
    public Fetcher provideBeerSearchFetcher(NetworkApi networkApi,
                                            NetworkRequestStatusStore networkRequestStatusStore,
                                            BeerStore beerStore,
                                            BeerSearchStore beerSearchStore) {
        return new BeerSearchFetcher(networkApi,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }

    @Provides
    @Named("topBeersFetcher")
    public Fetcher provideTopBeersFetcher(NetworkApi networkApi,
                                          NetworkRequestStatusStore networkRequestStatusStore,
                                          BeerStore beerStore,
                                          BeerSearchStore beerSearchStore) {
        return new TopBeersFetcher(networkApi,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }

    @Provides
    @Named("reviewFetcher")
    public Fetcher provideReviewFetcher(NetworkApi networkApi,
                                        NetworkRequestStatusStore networkRequestStatusStore,
                                        ReviewStore reviewStore,
                                        ReviewListStore reviewListStore) {
        return new ReviewFetcher(networkApi,
                networkRequestStatusStore::put,
                reviewStore,
                reviewListStore);
    }
}
