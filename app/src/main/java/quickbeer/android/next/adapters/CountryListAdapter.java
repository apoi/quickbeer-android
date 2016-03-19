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
package quickbeer.android.next.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reark.reark.utils.Log;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Country;
import quickbeer.android.next.views.listitems.CountryListItemViewHolder;

public class CountryListAdapter extends BaseListAdapter<CountryListItemViewHolder> {
    private static final String TAG = CountryListAdapter.class.getSimpleName();

    private final List<Country> sourceList;
    private final List<Country> adapterList = new ArrayList<>();

    public CountryListAdapter(Collection<Country> countries) {
        this.sourceList = new ArrayList<>(countries);
        Collections.sort(this.sourceList);

        this.adapterList.addAll(sourceList);
    }

    public void filterList(String filter) {
        Log.v(TAG, "filter(" + filter + ")");

        adapterList.clear();

        if (TextUtils.isEmpty(filter)) {
            adapterList.addAll(sourceList);
        } else {
            for (Country country : sourceList) {
                if (country.getName().toLowerCase().contains(filter.toLowerCase())) {
                    adapterList.add(country);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public CountryListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_item, parent, false);
        return new CountryListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryListItemViewHolder holder, int position) {
        holder.setItem(adapterList.get(position));
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }
}
