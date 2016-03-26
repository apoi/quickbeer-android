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
package quickbeer.android.next.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.BeerListAdapter;
import quickbeer.android.next.viewmodels.BaseViewModel;
import quickbeer.android.next.viewmodels.BeerListViewModel;
import quickbeer.android.next.viewmodels.BeerViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

public class BeerListView extends FrameLayout {
    private static final String TAG = BeerListView.class.getSimpleName();

    private RecyclerView beersListView;
    private BeerListAdapter beerListAdapter;
    private TextView searchStatusTextView;

    public BeerListView(Context context) {
        super(context, null);
    }

    public BeerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected RecyclerView getListView() {
        return beersListView;
    }

    protected BeerListAdapter getAdapter() {
        return beerListAdapter;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beersListView = (RecyclerView) findViewById(R.id.beers_list_view);
        beersListView.setHasFixedSize(true);

        beerListAdapter = new BeerListAdapter(Collections.emptyList());
        beersListView.setAdapter(beerListAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setRecycleChildrenOnDetach(true);
        beersListView.setLayoutManager(layoutManager);

        searchStatusTextView = (TextView) findViewById(R.id.search_status);
    }

    private void setBeers(@NonNull List<BeerViewModel> beers) {
        Preconditions.checkNotNull(beers, "Beer list cannot be null.");
        Preconditions.checkState(beerListAdapter != null, "Beer list adapter cannot be null.");

        Log.v(TAG, "Setting " + beers.size() + " beers to adapter");
        beerListAdapter.set(beers);
    }

    private void setProgressStatus(@NonNull BaseViewModel.ProgressStatus progressStatus) {
        switch (progressStatus) {
            case LOADING:
                searchStatusTextView.setText(R.string.search_status_loading);
                break;
            case ERROR:
                searchStatusTextView.setText(R.string.search_status_error);
                break;
            case IDLE:
                searchStatusTextView.setText("");
                break;
        }
    }

    /**
     * View binder between BeerListViewModel and the BeerListView
     */
    public static class ViewBinder extends RxViewBinder {
        private BeerListView view;
        private BeerListViewModel viewModel;

        public ViewBinder(@NonNull BeerListView view,
                          @NonNull BeerListViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getBeers()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setBeers));

            subscription.add(Observable.create(
                    subscriber -> {
                        view.beerListAdapter.setOnClickListener(this::beerListAdapterOnClick);
                        subscriber.add(Subscriptions.create(() -> view.beerListAdapter.setOnClickListener(null)));
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe());

            subscription.add(viewModel.getNetworkRequestStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setProgressStatus));
        }

        private void beerListAdapterOnClick(View clickedView) {
            final int itemPosition = view.beersListView.getChildAdapterPosition(clickedView);
            view.beerListAdapter
                    .getItem(itemPosition)
                    .getBeer()
                    .subscribe(beer -> {
                        viewModel.selectBeer(beer.getId());
                    });
        }
    }
}
