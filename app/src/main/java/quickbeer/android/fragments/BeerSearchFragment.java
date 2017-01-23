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
package quickbeer.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.features.home.SearchViewModel;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class BeerSearchFragment extends BeerListFragment {

    @Nullable
    @Inject
    SearchViewModel searchViewModel;

    @Nullable
    @Inject
    DataLayer.GetBeerSearch getBeerSearch;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            listDataBinder().bind(subscription);

            subscription.add(get(searchViewModel)
                    .getQueryStream()
                    .doOnNext(query -> Timber.d("query(%s)", query))
                    .switchMap(query -> get(getBeerSearch).call(query))
                    .subscribe(notification -> listViewModel().setNotification(notification),
                            Timber::e));
        }

        @Override
        public void unbind() {
            listDataBinder().unbind();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_list_fragment, container, false);
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        get(searchViewModel).setSearchHint(getResources().getString(R.string.search_box_hint_search_beers));
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
