/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.brewerdetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.R;
import quickbeer.android.core.activity.BindingDrawerActivity;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.viewmodels.SearchViewViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BrewerDetailsActivity extends BindingDrawerActivity {

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.collapsing_toolbar_background)
    ImageView imageView;

    @BindView(R.id.toolbar_overlay_gradient)
    View overlay;

    @Inject
    @Nullable
    DataLayer.GetBrewer getBrewer;

    @Inject
    @Nullable
    DataLayer.AccessBrewer accessBrewer;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @Nullable
    @Inject
    Picasso picasso;

    private int brewerId;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            ConnectableObservable<Brewer> sourceObservable = get(getBrewer)
                    .call(brewerId)
                    .subscribeOn(Schedulers.io())
                    .filter(DataStreamNotification::isOnNext)
                    .map(DataStreamNotification::getValue)
                    .first()
                    .publish();

            // Update brewer access date
            subscription.add(sourceObservable
                    .map(Brewer::id)
                    .subscribe(get(accessBrewer)::call, Timber::e));

            // Set toolbar title
            subscription.add(sourceObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BrewerDetailsActivity.this::setToolbarDetails, Timber::e));

            subscription.add(sourceObservable
                    .connect());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.collapsing_toolbar_activity);

        ButterKnife.bind(this);

        setupDrawerLayout();

        setBackNavigationEnabled(true);

        if (savedInstanceState != null) {
            brewerId = savedInstanceState.getInt("brewerId");
        } else {
            brewerId = getIntent().getIntExtra("brewerId", 0);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BrewerDetailsPagerFragment())
                    .commit();
        }
    }

    private void setToolbarDetails(@NonNull Brewer brewer) {
        collapsingToolbar.setTitle(brewer.name());
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
        outState.putInt("brewerId", brewerId);
    }

    public int getBrewerId() {
        return brewerId;
    }

    @Override
    protected void navigateTo(@NonNull MenuItem menuItem) {
        get(navigationProvider).navigateWithNewActivity(menuItem);
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
