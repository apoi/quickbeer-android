package quickbeer.android.next.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;

import io.reark.reark.utils.Log;
import quickbeer.android.next.fragments.MainFragment;
import rx.Subscription;

public class MainActivity extends ActivityBase {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        getQueryObservable().subscribe(
                query -> {
                    Log.d(TAG, "query(" + query + ")");
                    Intent intent = new Intent(this, BeerSearchActivity.class);
                    intent.putExtra("query", query);
                    startActivityForResult(intent, 0);
                },
                throwable -> {
                    Log.e(TAG, "error", throwable);
                });
    }

    @Override
    protected Fragment getFragment() {
        return new MainFragment();
    }
}
