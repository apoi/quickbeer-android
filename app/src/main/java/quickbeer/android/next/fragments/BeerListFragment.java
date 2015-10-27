package quickbeer.android.next.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.view.BeerListView;
import quickbeer.android.next.viewmodels.BeerListViewModel;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerListFragment extends Fragment {
    private static final String TAG = BeerListFragment.class.getSimpleName();

    private BeerListView.ViewBinder beersViewBinder;

    @Inject
    BeerListViewModel beerListViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);

        beerListViewModel.getSelectBeer()
                .subscribe(beer -> {
                    Log.d(TAG, "Selected beer " + beer.getName());
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beersViewBinder = new BeerListView.ViewBinder((BeerListView) view.findViewById(R.id.beers_view), beerListViewModel);
        beerListViewModel.subscribeToDataStore();
    }

    @Override
    public void onResume() {
        super.onResume();
        beersViewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        beersViewBinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beerListViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beerListViewModel.dispose();
        beerListViewModel = null;
    }
}
