package quickbeer.android.next.viewmodels;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.data.DataLayer.GetBeer;

/**
 * Created by antti on 25.10.2015.
 */
@Module
public class ViewModelModule {
    @Provides
    public BeerListViewModel provideBeersViewModel(GetBeer getBeer) {
        return new BeerListViewModel(getBeer);
    }
}
