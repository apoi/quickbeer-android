package quickbeer.android.next.viewmodels;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.data.DataLayer.GetBeer;
import quickbeer.android.next.data.DataLayer.GetTopBeers;

/**
 * Created by antti on 25.10.2015.
 */
@Module
public class ViewModelModule {
    @Provides
    public BeersViewModel provideBeersViewModel(GetTopBeers getTopBeers, GetBeer getBeer) {
        return new BeersViewModel(getTopBeers, getBeer);
    }
}
