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

import android.support.v7.widget.RecyclerView;
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
import quickbeer.android.next.pojo.SimpleItem;
import quickbeer.android.next.views.viewholders.SimpleListItemViewHolder;

public class SimpleListAdapter extends BaseListAdapter {
    private static final String TAG = SimpleListAdapter.class.getSimpleName();

    private final List<SimpleItem> sourceList;
    private final List<SimpleItem> adapterList = new ArrayList<>();

    public SimpleListAdapter(Collection<SimpleItem> countries) {
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
            for (SimpleItem item : sourceList) {
                if (item.getName().toLowerCase().contains(filter.toLowerCase())) {
                    adapterList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false);
        return new SimpleListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SimpleListItemViewHolder) holder).setItem(adapterList.get(position));
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }
}
