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
import android.view.MenuItem;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.R;
import quickbeer.android.core.activity.DrawerActivity;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.features.main.MainActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.viewmodels.SearchViewViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsActivity extends DrawerActivity {

    private int beerId;

    @Inject
    @Nullable
    DataLayer.GetBeer getBeer;

    @Inject
    @Nullable
    BeerMetadataStore metadataStore;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            ConnectableObservable<DataStreamNotification<Beer>> sourceObservable =
                    get(getBeer).call(beerId).publish();

            // Update beer access date
            subscription.add(sourceObservable
                    .filter(DataStreamNotification::isOnNext)
                    .map(DataStreamNotification::getValue)
                    .first()
                    .map(BeerMetadata::newAccess)
                    .subscribe(get(metadataStore)::put, Timber::e));

            // Set activity title
            subscription.add(sourceObservable
                    .filter(DataStreamNotification::isOnNext)
                    .map(DataStreamNotification::getValue)
                    .first()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(Beer::name)
                    .subscribe(BeerDetailsActivity.this::setTitle, Timber::e));

            subscription.add(sourceObservable
                    .connect());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beer_details_activity);

        setupDrawerLayout();
        setBackNavigationEnabled(true);

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BeerDetailsFragment())
                    .commit();
        }
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @NonNull
    @Override
    protected ViewModel viewModel() {
        return get(searchViewViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("beerId", beerId);
    }

    public int getBeerId() {
        return beerId;
    }

    @Override
    protected void navigateTo(@NonNull final MenuItem menuItem) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("menuNavigationId", menuItem.getItemId());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Timber.d("onBackPressed");

        checkNotNull(navigationProvider);

        if (navigationProvider.canNavigateBack()) {
            navigationProvider.navigateBack();
        } else {
            super.onBackPressed();
        }
    }
}
