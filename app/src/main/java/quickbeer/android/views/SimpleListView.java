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
package quickbeer.android.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import quickbeer.android.R;
import quickbeer.android.adapters.SimpleListAdapter;
import quickbeer.android.utils.SimpleListSource;
import rx.Observable;

public class SimpleListView extends FrameLayout {
    private SimpleListAdapter simpleListAdapter;

    public SimpleListView(Context context) {
        super(context);
    }

    public SimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListSource(SimpleListSource source) {
        simpleListAdapter = new SimpleListAdapter(source.getList());

        RecyclerView simpleListView = (RecyclerView) findViewById(R.id.simple_list_view);
        simpleListView.setHasFixedSize(true);
        simpleListView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleListView.setAdapter(simpleListAdapter);
    }

    public void setFilterObservable(Observable<String> filterObservable) {
        filterObservable.subscribe(simpleListAdapter::filterList);
    }
}
