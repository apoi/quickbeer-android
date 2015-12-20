package quickbeer.android.next.fragments;

import android.os.Bundle;

import javax.inject.Inject;

import io.reark.reark.utils.Log;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.activities.ActivityBase;
import quickbeer.android.next.data.DataLayer;

/**
 * Created by antti on 16.11.2015.
 */
public class BeerSearchFragment extends BeerListFragment {
    private static final String TAG = BeerSearchFragment.class.getSimpleName();

    @Inject
    DataLayer.GetBeerSearch getBeerSearch;

    @Override
    public int getLayout() {
        return R.layout.beer_list_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);

        ((ActivityBase) getActivity())
                .getQueryObservable()
                .subscribe(
                        query -> {
                            Log.d(TAG, "query(" + query + ")");
                            setSourceObservable(getBeerSearch.call(query));
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                        });
    }
}
