package quickbeer.android.next.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.viewmodels.BeerListViewModel;
import quickbeer.android.next.views.BeerListView;
import rx.Observable;
import rx.Subscription;

public class BeerListFragment extends Fragment {
    private static final String TAG = BeerListFragment.class.getSimpleName();

    private BeerListView.ViewBinder beersViewBinder;
    private Subscription selectBeerSubscription;

    @Inject
    BeerListViewModel beerListViewModel;

    public int getLayout() {
        return R.layout.beer_list_fragment;
    }

    public void setSourceObservable(Observable<DataStreamNotification<BeerSearch>> sourceObservable) {
        // Unsubscribe old source before setting the new one, otherwise the subscribe
        // call assumes the old subscription to still be valid.
        beerListViewModel.unsubscribeFromDataStore();
        beerListViewModel.setSourceObservable(sourceObservable);
        beerListViewModel.subscribeToDataStore();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beersViewBinder = new BeerListView.ViewBinder((BeerListView) view.findViewById(R.id.beers_view), beerListViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        beersViewBinder.bind();

        selectBeerSubscription = beerListViewModel
                .getSelectBeer()
                .subscribe(beerId -> {
                    Log.d(TAG, "Selected beer " + beerId);

                    Intent intent = new Intent(getActivity(), BeerDetailsActivity.class);
                    intent.putExtra("beerId", beerId);
                    startActivity(intent);
                });

    }

    @Override
    public void onPause() {
        super.onPause();
        beersViewBinder.unbind();

        selectBeerSubscription.unsubscribe();
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
