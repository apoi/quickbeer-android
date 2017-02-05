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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.R;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.utils.Countries;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.views.SimpleListView;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class CountryListFragment extends BindingBaseFragment {

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @Inject
    Countries countries;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel()
                    .getQueryStream()
                    .subscribe(getView()::setFilter, Timber::e));

            subscription.add(getView()
                    .selectionStream()
                    .subscribe(CountryListFragment.this::navigateToCountry, Timber::e));
        }
    };

    private void navigateToCountry(@NonNull final Integer countryId) {
        Timber.d("navigateToCountry(" + countryId + ")");

        Bundle bundle = new Bundle();
        bundle.putString("country", String.valueOf(countryId));

        get(navigationProvider).addPage(Page.COUNTRY, bundle);
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkNotNull(searchViewViewModel);

        getView().setListSource(countries);

        searchViewViewModel.setLiveFilteringEnabled(true);
        searchViewViewModel.setConventOverlayEnabled(false);
        searchViewViewModel.setMinimumSearchLength(-1);
        searchViewViewModel.setSearchHint(get(resourceProvider).getString(R.string.search_box_hint_filter_countries));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_list_fragment, container, false);
    }

    @Override
    @NonNull
    public SimpleListView getView() {
        return (SimpleListView) get(super.getView());
    }

    @NonNull
    @Override
    protected SearchViewViewModel viewModel() {
        return get(searchViewViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
