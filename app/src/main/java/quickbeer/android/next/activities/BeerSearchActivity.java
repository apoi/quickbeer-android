package quickbeer.android.next.activities;

import android.support.v4.app.Fragment;

import quickbeer.android.next.fragments.BeerSearchFragment;

/**
 * Created by antti on 17.11.2015.
 */
public class BeerSearchActivity extends ActivityBase {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected Fragment getFragment() {
        BeerSearchFragment fragment = new BeerSearchFragment();
        String query = getIntent().getStringExtra("query");
        fragment.setQueryObservable(getQueryObservable().startWith(query));
        return fragment;
    }
}
