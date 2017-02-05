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
package quickbeer.android.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.data.pojos.SimpleItem;
import quickbeer.android.views.viewholders.SimpleListItemViewHolder;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.none;
import static polanski.option.Option.ofObj;

public class SimpleListAdapter extends BaseListAdapter {

    @NonNull
    private final List<SimpleItem> sourceList;

    @NonNull
    private final List<SimpleItem> adapterList = new ArrayList<>(0);

    @Nullable
    private View.OnClickListener onClickListener;

    public SimpleListAdapter(@NonNull final Collection<SimpleItem> countries) {
        sourceList = new ArrayList<>(get(countries));
        Collections.sort(sourceList);

        adapterList.addAll(sourceList);
    }

    public void filterList(String filter) {
        Timber.v("filter(" + filter + ")");

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

    public void setOnClickListener(@Nullable final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item, parent, false);
        return new SimpleListItemViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SimpleListItemViewHolder) holder).setItem(adapterList.get(position));
    }

    @NonNull
    public Option<SimpleItem> getItemAt(int index) {
        if (index >= getItemCount() || index < 0) {
            return none();
        }

        return ofObj(adapterList.get(index));
    }

    @Override
    public int getItemCount() {
        return adapterList.size();
    }
}
