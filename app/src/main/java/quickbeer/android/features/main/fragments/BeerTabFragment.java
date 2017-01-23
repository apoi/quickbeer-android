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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Header;
import quickbeer.android.views.BeerListView;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class BeerTabFragment extends BeerListFragment {

    @Inject
    DataLayer.GetAccessedBeers getAccessedBeers;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            listDataBinder().bind(subscription);

            subscription.add(getAccessedBeers.call()
                    .doOnNext(query -> Timber.d("getTopBeers finished"))
                    .subscribe(notification -> listViewModel().setNotification(notification),
                            Timber::e));
        }

        @Override
        public void unbind() {
            listDataBinder().unbind();
        }
    };

    @Override
    public int getLayout() {
        return R.layout.beer_tab_fragment;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((BeerListView) getView()).setHeader(new Header(getContext().getString(R.string.recent_beers)));
    }

    @NonNull
    @Override
    protected ViewModel viewModel() {
        return listViewModel();
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
