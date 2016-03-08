package quickbeer.android.next.fragments;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.data.DataLayer;

public class MainFragment extends BeerListFragment {
    @Inject
    DataLayer.GetTopBeers getTopBeers;

    @Override
    public int getLayout() {
        return R.layout.main_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QuickBeer.getInstance().getGraph().inject(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSourceObservable(getTopBeers.call());
    }
}
