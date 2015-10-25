package quickbeer.android.next.injections;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.activities.MainActivity;
import quickbeer.android.next.data.DataStoreModule;
import quickbeer.android.next.fragments.BeerListFragment;
import quickbeer.android.next.network.NetworkService;
import quickbeer.android.next.viewmodels.ViewModelModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DataStoreModule.class,
        ViewModelModule.class,
        InstrumentationModule.class
})
public interface Graph {

    void inject(QuickBeer quickBeer);
    void inject(NetworkService networkService);
    void inject(MainActivity mainActivity);
    void inject(BeerListFragment beerListFragment);

    final class Initializer {
        public static Graph init(Application application) {
            return DaggerGraph.builder()
                    .applicationModule(new ApplicationModule(application))
                    .build();
        }
    }
}
