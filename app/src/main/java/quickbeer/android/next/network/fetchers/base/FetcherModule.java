package quickbeer.android.next.network.fetchers.base;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.data.store.BeerSearchStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.NetworkModule;
import quickbeer.android.next.network.fetchers.BeerSearchFetcher;
import quickbeer.android.next.network.fetchers.TopBeersFetcher;

@Module(includes = NetworkModule.class)
public final class FetcherModule {
    @Provides
    @Named("beerSearch")
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
    @Named("topBeers")
    public Fetcher provideTopBeersFetcher(NetworkApi networkApi,
                                          NetworkRequestStatusStore networkRequestStatusStore,
                                          BeerStore beerStore,
                                          BeerSearchStore beerSearchStore) {
        return new TopBeersFetcher(networkApi,
                networkRequestStatusStore::put,
                beerStore,
                beerSearchStore);
    }
}
