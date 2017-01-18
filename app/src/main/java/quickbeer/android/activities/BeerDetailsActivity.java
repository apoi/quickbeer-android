/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.activities.base.SearchActivity;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.fragments.BeerDetailsFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsActivity extends SearchActivity {
    private static final String TAG = BeerDetailsActivity.class.getSimpleName();

    private int beerId;

    @NonNull
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Inject
    @Nullable
    DataLayer.GetBeer getBeer;

    @Inject
    @Nullable
    BeerMetadataStore metadataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);
        }

        ConnectableObservable<DataStreamNotification<Beer>> sourceObservable =
                get(getBeer).call(beerId).publish();

        // Pass to the activity progress indicator
        addProgressObservable(sourceObservable);

        // Update beer access date
        compositeSubscription.add(sourceObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .map(BeerMetadata::newAccess)
                .subscribe(get(metadataStore)::put,
                        Log.onError(TAG, "error updating access date")));

        // Set activity title
        compositeSubscription.add(sourceObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .observeOn(AndroidSchedulers.mainThread())
                .map(Beer::name)
                .subscribe(this::setTitle,
                        Log.onError(TAG, "error getting beer")));

        compositeSubscription.add(getQueryObservable()
                .subscribe(
                        query -> {
                            Log.d(TAG, "query(" + query + ")");

                            Intent intent = new Intent(this, BeerSearchActivity.class);
                            intent.putExtra("query", query);
                            startActivity(intent);
                        },
                        Log.onError(TAG, "error in query")));

        compositeSubscription.add(sourceObservable
                .connect());
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();

        super.onDestroy();
    }

    @Override
    protected void inject() {
        super.inject();
        getGraph().inject(this);
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
