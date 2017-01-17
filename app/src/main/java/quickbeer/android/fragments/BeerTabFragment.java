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
import android.view.View;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.R;
import quickbeer.android.data.DataLayer;
import quickbeer.android.pojo.Header;
import quickbeer.android.pojo.ItemList;
import quickbeer.android.views.BeerListView;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class BeerTabFragment extends BeerListFragment {
    @Inject
    DataLayer.GetAccessedBeers getAccessedBeers;

    private Subscription subscription;

    private final BehaviorSubject<DataStreamNotification<ItemList<String>>> accessedBeersSubject = BehaviorSubject.create();

    @Override
    public int getLayout() {
        return R.layout.beer_tab_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGraph().inject(this);

        // BeerTabFragment never goes away, so we can keep a perpetual subscription
        subscription = getAccessedBeers.call()
                .doOnNext(notification -> Log.d("___", "accessed: " + notification))
                .subscribe(accessedBeersSubject::onNext);

        setSource(accessedBeersSubject.asObservable());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((BeerListView) view).setHeader(new Header(getContext().getString(R.string.recent_beers)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        subscription.unsubscribe();
    }
}
