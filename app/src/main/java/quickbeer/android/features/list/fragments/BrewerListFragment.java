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
import quickbeer.android.viewmodels.BrewerListViewModel;
import quickbeer.android.viewmodels.BrewerViewModel;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.viewmodels.SearchViewViewModel.Mode;
import quickbeer.android.views.BrewerListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public abstract class BrewerListFragment extends BindingBaseFragment {

    @Nullable
    @BindView(R.id.list_layout)
    BrewerListView view;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    // TODO inject global ProgressViewModel?

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            subscription.add(get(view)
                    .selectedBrewerStream()
                    .doOnNext(brewerId -> Timber.d("Selected brewer " + brewerId))
                    .subscribe(brewerId -> openBrewerDetails(brewerId), Timber::e));

            subscription.add(viewModel()
                    .getBrewers()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BrewerListFragment.this::handleResultList, Timber::e));

            subscription.add(viewModel()
                    .getProgressStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(get(view)::setProgressStatus, Timber::e));

            subscription.add(get(searchViewViewModel)
                    .getQueryStream()
                    .subscribe(BrewerListFragment.this::onQuery, Timber::e));
        }
    };

    private void handleResultList(@NonNull List<BrewerViewModel> brewerList) {
        if (brewerList.size() == 1 && singleResultShouldOpenDetails()) {
            openBrewerDetails(brewerList.get(0).getBrewerId());
        } else {
            get(view).setBrewers(brewerList);
        }
    }

    protected boolean singleResultShouldOpenDetails() {
        return false;
    }

    protected void openBrewerDetails(@NonNull Integer brewerId) {
        Intent intent = new Intent(getActivity(), BeerDetailsActivity.class); // TODO
        intent.putExtra("brewerId", brewerId);
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
                get(resourceProvider).getString(R.string.search_box_hint_search_brewers));
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    protected int getLayout() {
        return R.layout.brewer_list_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    @NonNull
    protected abstract BrewerListViewModel viewModel();

    protected abstract void onQuery(@NonNull String query);

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    // TODO remove
    @NonNull
    protected DataBinder listDataBinder() {
        return dataBinder;
    }

}
