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

import android.os.Bundle;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.BeerSearch;
import rx.subjects.BehaviorSubject;

public class MainFragment extends BeerListFragment {
    @Inject
    DataLayer.GetAccessedBeers getAccessedBeers;

    private BehaviorSubject<DataStreamNotification<BeerSearch>> accessedBeersSubject = BehaviorSubject.create();

    @Override
    public int getLayout() {
        return R.layout.main_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGraph().inject(this);

        // MainFragment never goes away, so we can keep a perpetual subscription
        getAccessedBeers.call()
                .subscribe(accessedBeersSubject::onNext);

        setSource(accessedBeersSubject.asObservable());
    }
}
