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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import javax.inject.Inject;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.CountryListAdapter;
import quickbeer.android.next.utils.Countries;

public class CountryListView extends FrameLayout {
    private RecyclerView countryListView;
    private CountryListAdapter countryListAdapter;

    @Inject
    Countries countries;

    public CountryListView(Context context) {
        super(context);
    }

    public CountryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        QuickBeer.getInstance().getGraph().inject(this);

        countryListAdapter = new CountryListAdapter(countries.getCountries());

        countryListView = (RecyclerView) findViewById(R.id.countries_list_view);
        countryListView.setHasFixedSize(true);
        countryListView.setLayoutManager(new LinearLayoutManager(getContext()));
        countryListView.setAdapter(countryListAdapter);
    }
}
