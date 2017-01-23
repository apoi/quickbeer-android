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
package quickbeer.android.features.beer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.activity.BeerSearchActivity;
import quickbeer.android.activity.base.SearchActivity;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.features.beer.BeerDetailsFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsActivity extends SearchActivity {

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
                .subscribe(get(metadataStore)::put, Timber::e));

        // Set activity title
        compositeSubscription.add(sourceObservable
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue)
                .first()
                .observeOn(AndroidSchedulers.mainThread())
                .map(Beer::name)
                .subscribe(this::setTitle, Timber::e));

        compositeSubscription.add(getQueryObservable()
                .subscribe(
                        query -> {
                            Timber.d("query(" + query + ")");

                            Intent intent = new Intent(this, BeerSearchActivity.class);
                            intent.putExtra("query", query);
                            startActivity(intent);
                        },
                        Timber::e));

        compositeSubscription.add(sourceObservable
                .connect());
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();

        super.onDestroy();
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
