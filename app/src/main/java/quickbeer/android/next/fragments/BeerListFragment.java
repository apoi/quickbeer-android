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
package quickbeer.android.next.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.activities.base.SearchBarActivity;
import quickbeer.android.next.fragments.base.BaseFragment;
import quickbeer.android.next.pojo.ItemList;
import quickbeer.android.next.viewmodels.BeerListViewModel;
import quickbeer.android.next.views.BeerListView;
import rx.Observable;
import rx.Subscription;

public class BeerListFragment extends BaseFragment {
    private static final String TAG = BeerListFragment.class.getSimpleName();

    private BeerListView.ViewBinder beersViewBinder;
    private Subscription selectBeerSubscription;

    @Inject
    BeerListViewModel beerListViewModel;

    public int getLayout() {
        return R.layout.beer_list_fragment;
    }

    public void setSource(Observable<DataStreamNotification<ItemList<String>>> sourceObservable) {
        // Unsubscribe old source before setting the new one, otherwise the subscribe
        // call assumes the old subscription to still be valid.
        beerListViewModel.unsubscribeFromDataStore();
        beerListViewModel.setSourceObservable(sourceObservable);
        beerListViewModel.subscribeToDataStore();
    }

    public void setProgressingSource(Observable<DataStreamNotification<ItemList<String>>> sourceObservable) {
        Observable<DataStreamNotification<ItemList<String>>> observable =
                sourceObservable.publish().refCount();

        setSource(observable);

        // Hook up the search observable status to progress indicator
        ((SearchBarActivity) getActivity()).addProgressObservable(observable);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        beersViewBinder = new BeerListView.ViewBinder((BeerListView) view.findViewById(R.id.list_layout), beerListViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        beersViewBinder.bind();

        selectBeerSubscription = beerListViewModel
                .getSelectBeer()
                .subscribe(beerId -> {
                    Log.d(TAG, "Selected beer " + beerId);

                    Intent intent = new Intent(getActivity(), BeerDetailsActivity.class);
                    intent.putExtra("beerId", beerId);
                    startActivity(intent);
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        beersViewBinder.unbind();
        selectBeerSubscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beerListViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beerListViewModel.dispose();
        beerListViewModel = null;
    }
}
