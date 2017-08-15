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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.Constants;
import quickbeer.android.R;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.injections.IdModule;
import quickbeer.android.listeners.LoadMoreListener;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BeerReviewsFragment extends BindingBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.beer_reviews_view)
    BeerReviewsView reviewsView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    BeerDetailsViewModel beerDetailsViewModel;

    @NonNull
    private final LoadMoreListener loadMoreListener = new LoadMoreListener();

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    private int beerId;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel()
                    .getReviews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(reviewsView::setReviews, Timber::e));

            subscription.add(loadMoreListener.moreItemsRequestedStream()
                    .subscribe(viewModel()::loadMoreReviews, Timber::e));
        }
    };

    @NonNull
    public static Fragment newInstance(int beerId) {
        BeerReviewsFragment fragment = new BeerReviewsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID_KEY, beerId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void inject() {
        getComponent()
                .plusId(new IdModule(beerId))
                .inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState != null
                ? savedInstanceState
                : getArguments();

        ofObj(bundle)
                .map(state -> state.getInt(Constants.ID_KEY))
                .ifSome(value -> beerId = value)
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_details_fragment_reviews, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
        reviewsView.setOnScrollListener(loadMoreListener);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.ID_KEY, beerId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @NonNull
    @Override
    protected BeerDetailsViewModel viewModel() {
        return get(beerDetailsViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

    @Override
    public void onRefresh() {
        loadMoreListener.resetState();
        viewModel().refreshReviews();
        swipeRefreshLayout.setRefreshing(false);
    }
}
