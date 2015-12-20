package quickbeer.android.next.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import quickbeer.android.next.fragments.BeerSearchFragment;
import rx.Observable;

/**
 * Created by antti on 9.12.2015.
 */
public class BeerDetailsActivity extends ActivityBase {
    private static final String TAG = BeerDetailsActivity.class.getSimpleName();

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            query = savedInstanceState.getString("beerId");
        } else {
            query = getIntent().getStringExtra("beerId");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("beerId", query);
    }

    @Override
    protected Fragment getFragment() {
        return new BeerSearchFragment();
    }
}
