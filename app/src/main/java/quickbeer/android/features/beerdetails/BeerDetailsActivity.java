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
package quickbeer.android.features.beerdetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback.EmptyCallback;
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
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.features.photoview.PhotoViewActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.transformations.BlurTransformation;
import quickbeer.android.transformations.ContainerLabelExtractor;
import quickbeer.android.viewmodels.SearchViewViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsActivity extends BindingDrawerActivity {

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.collapsing_toolbar_background)
    ImageView imageView;

    @BindView(R.id.toolbar_overlay_gradient)
    View overlay;

    @Inject
    @Nullable
    DataLayer.GetBeer getBeer;

    @Inject
    @Nullable
    DataLayer.AccessBeer accessBeer;

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

    private int beerId;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            ConnectableObservable<Beer> sourceObservable = get(getBeer)
                    .call(beerId)
                    .subscribeOn(Schedulers.io())
                    .filter(DataStreamNotification::isOnNext)
                    .map(DataStreamNotification::getValue)
                    .first()
                    .publish();

            // Update beer access date
            subscription.add(sourceObservable
                    .map(Beer::id)
                    .subscribe(get(accessBeer)::call, Timber::e));

            // Update brewer access date
            subscription.add(sourceObservable
                    .map(Beer::brewerId)
                    .subscribe(get(accessBrewer)::call, Timber::e));

            // Set toolbar title
            subscription.add(sourceObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BeerDetailsActivity.this::setToolbarDetails, Timber::e));

            subscription.add(sourceObservable
                    .connect());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.beer_details_activity);

        ButterKnife.bind(this);

        setupDrawerLayout();

        setBackNavigationEnabled(true);

        imageView.setOnClickListener(__ -> get(toastProvider).showToast(R.string.beer_details_no_photo));

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId");
        } else {
            beerId = getIntent().getIntExtra("beerId", 0);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BeerDetailsPagerFragment())
                    .commit();
        }
    }

    private void setToolbarDetails(@NonNull Beer beer) {
        collapsingToolbar.setTitle(beer.name());

        get(picasso)
                .load(beer.getImageUri())
                .transform(new ContainerLabelExtractor(300, 300))
                .transform(new BlurTransformation(getApplicationContext(), 15))
                .into(imageView, new EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        overlay.setVisibility(View.VISIBLE);
                        imageView.setOnClickListener(__ -> openPhotoView(beer.getImageUri()));
                    }
                });
    }

    private void openPhotoView(@NonNull String uri) {
        Intent intent = new Intent(this, PhotoViewActivity.class);
        intent.putExtra("source", uri);
        startActivity(intent);
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
