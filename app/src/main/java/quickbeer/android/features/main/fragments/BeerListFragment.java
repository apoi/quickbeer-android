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
package quickbeer.android.features.main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.features.beer.BeerDetailsActivity;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.viewmodels.BeerListViewModel;
import quickbeer.android.views.BeerListView;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public abstract  class BeerListFragment extends BindingBaseFragment {

    @Nullable
    @Inject
    BeerListViewModel beerListViewModel;

    // TODO inject global ProgressViewModel?

    private BeerListView.ViewBinder beersViewBinder;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            subscription.add(listViewModel()
                    .selectedBeerStream()
                    .doOnNext(beerId -> Timber.d("Selected beer " + beerId))
                    .subscribe(beerId -> openBeerDetails(beerId), Timber::e));

            beersViewBinder.bind();
        }

        @Override
        public void unbind() {
            beersViewBinder.unbind();
        }
    };

    private void openBeerDetails(@NonNull final Integer beerId) {
        Intent intent = new Intent(getActivity(), BeerDetailsActivity.class);
        intent.putExtra("beerId", beerId);
        startActivity(intent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BeerListView beerListView = (BeerListView) getView().findViewById(R.id.list_layout);
        beersViewBinder = new BeerListView.ViewBinder(beerListView, listViewModel());
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    public int getLayout() {
        return R.layout.beer_list_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @NonNull
    protected DataBinder listDataBinder() {
        return dataBinder;
    }

    @NonNull
    protected BeerListViewModel listViewModel() {
        return get(beerListViewModel);
    }

}
