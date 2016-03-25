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
package quickbeer.android.next.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.next.activities.base.SearchActivity;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.fragments.BeerDetailsFragment;
import quickbeer.android.next.pojo.Beer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;

public class BeerDetailsActivity extends SearchActivity {
    private static final String TAG = BeerDetailsActivity.class.getSimpleName();

    private int beerId;

    @Inject
    DataLayer.GetBeer getBeer;

    @Inject
    DataLayer.AccessBeer accessBeer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);
        }

        ConnectableObservable<DataStreamNotification<Beer>> sourceObservable =
                getBeer.call(beerId).publish();

        // Pass to the activity progress indicator
        addProgressObservable(sourceObservable);

        // Update beer access date
        sourceObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .subscribe(beer -> accessBeer.call(beer.getId()));

        // Set activity title
        sourceObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        beer -> setTitle(beer.getName()),
                        throwable -> {
                            Log.e(TAG, "error getting beer", throwable);
                        });

        sourceObservable.connect();

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
    protected void onDestroy() {
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
