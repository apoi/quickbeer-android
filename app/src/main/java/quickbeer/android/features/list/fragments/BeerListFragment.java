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
package quickbeer.android.features.list.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.features.beerdetails.BeerDetailsActivity;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.viewmodels.BeerListViewModel;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.viewmodels.SearchViewViewModel.Mode;
import quickbeer.android.views.BeerListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public abstract class BeerListFragment extends BindingBaseFragment {

    @BindView(R.id.list_layout)
    BeerListView view;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            subscription.add(get(view)
                    .selectedBeerStream()
                    .doOnNext(beerId -> Timber.d("Selected beer " + beerId))
                    .subscribe(beerId -> openBeerDetails(beerId), Timber::e));

            subscription.add(viewModel()
                    .getBeers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BeerListFragment.this::handleResultList, Timber::e));

            subscription.add(viewModel()
                    .getProgressStatus()
                    .map(BeerListFragment.this::toStatusValue)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setProgressStatus, Timber::e));

            subscription.add(get(searchViewViewModel)
                    .getQueryStream()
                    .subscribe(BeerListFragment.this::onQuery, Timber::e));
        }
    };

    protected void handleResultList(@NonNull List<BeerViewModel> beerList) {
        if (beerList.size() == 1 && singleResultShouldOpenDetails()) {
            openBeerDetails(beerList.get(0).getBeerId());
        } else {
            view.setBeers(beerList);
        }
    }

    protected boolean singleResultShouldOpenDetails() {
        return false;
    }

    protected void openBeerDetails(@NonNull Integer beerId) {
        Intent intent = new Intent(getActivity(), BeerDetailsActivity.class);
        intent.putExtra("beerId", beerId);
        startActivity(intent);
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checkNotNull(searchViewViewModel);

        searchViewViewModel.setMode(Mode.SEARCH,
                get(resourceProvider).getString(R.string.search_box_hint_search_beers));
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    protected int getLayout() {
        return R.layout.beer_list_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @NonNull
    protected String toStatusValue(@NonNull ProgressStatus progressStatus) {
        switch (progressStatus) {
            case LOADING:
                return getString(R.string.search_status_loading);
            case ERROR:
                return getString(R.string.search_status_error);
            case EMPTY:
                return getString(R.string.search_no_results);
            default:
                return "";
        }
    }

    @Override
    @NonNull
    protected abstract BeerListViewModel viewModel();

    protected abstract void onQuery(@NonNull String query);

    @Override
    @NonNull
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
