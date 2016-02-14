package quickbeer.android.next.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import io.reark.reark.utils.Log;
import quickbeer.android.next.fragments.BeerDetailsFragment;

/**
 * Created by antti on 9.12.2015.
 */
public class BeerDetailsActivity extends ActivityBase {
    private static final String TAG = BeerDetailsActivity.class.getSimpleName();

    private int beerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);
        }

        getQueryObservable().subscribe(
                query -> {
                    Log.d(TAG, "query(" + query + ")");

                    Intent intent = new Intent(this, BeerSearchActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                },
                throwable -> {
                    Log.e(TAG, "error", throwable);
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("beerId", beerId);
    }

    @Override
    protected Fragment getFragment() {
        return new BeerDetailsFragment();
    }

    public int getBeerId() {
        return beerId;
    }
}
