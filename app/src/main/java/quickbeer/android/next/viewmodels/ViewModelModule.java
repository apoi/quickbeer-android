package quickbeer.android.next.viewmodels;

import dagger.Module;
import dagger.Provides;
import quickbeer.android.next.data.DataLayer.GetBeer;

@Module
public class ViewModelModule {
    @Provides
    public BeerListViewModel provideBeersViewModel(GetBeer getBeer) {
        return new BeerListViewModel(getBeer);
    }
}
