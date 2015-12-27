package quickbeer.android.next.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import quickbeer.android.next.fragments.BeerSearchFragment;
import rx.Observable;

/**
 * Created by antti on 17.11.2015.
 */
public class BeerSearchActivity extends ActivityBase {
    private static final String TAG = BeerSearchActivity.class.getSimpleName();

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
        } else {
            query = getIntent().getStringExtra("query");
        }

        // Toolbar title reflects the search query
        getQueryObservable()
                .doOnNext(s -> query = s)
                .subscribe(this::setTitle);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
    }

    @Override
    public Observable<String> getQueryObservable() {
        // This activity always starts with a submitted query
        return super.getQueryObservable()
                .startWith(query);
    }

    @Override
    protected Fragment getFragment() {
        return new BeerSearchFragment();
    }
}
