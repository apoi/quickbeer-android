package quickbeer.android.next.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.fragments.BeerDetailsFragment;

/**
 * Created by antti on 9.12.2015.
 */
public class BeerDetailsActivity extends ActivityBase {
    private static final String TAG = BeerDetailsActivity.class.getSimpleName();

    private int beerId;

    @Inject
    DataLayer.GetBeer getBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);
        }

        // Set the title for activity
        getBeer.call(beerId)
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .subscribe(
                        beer -> setTitle(beer.getName()),
                        throwable -> {
                            Log.e(TAG, "error getting beer", throwable);
                        });

        getQueryObservable().subscribe(
                query -> {
                    Log.d(TAG, "query(" + query + ")");

                    Intent intent = new Intent(this, BeerSearchActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                },
                throwable -> {
                    Log.e(TAG, "error in query", throwable);
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
