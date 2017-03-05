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
package quickbeer.android.features.list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.R;
import quickbeer.android.adapters.BaseListAdapter;
import quickbeer.android.data.pojos.Header;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.views.viewholders.BeerViewHolder;
import quickbeer.android.views.viewholders.HeaderViewHolder;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerListAdapter extends BaseListAdapter {

    @Nullable
    private Header header;

    @NonNull
    private final List<BeerViewModel> beers = new ArrayList<>(10);

    @NonNull
    private final List<Object> items = new ArrayList<>(10);

    @Nullable
    private View.OnClickListener onClickListener;

    public void setHeader(@NonNull Header header) {
        this.header = get(header);

        recreateList();
    }

    private void recreateList() {
        items.clear();

        if (header != null) {
            items.add(header);
        }

        items.addAll(beers);

        notifyDataSetChanged();
    }

    public void setOnClickListener(@NonNull View.OnClickListener onClickListener) {
        this.onClickListener = get(onClickListener);
    }

    public BeerViewModel getBeerViewModel(int position) {
        return (BeerViewModel) items.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_list_item, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_list_item, parent, false);
            return new BeerViewHolder(v, get(onClickListener));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        if (type == ItemType.HEADER) {
            ((HeaderViewHolder) holder).setHeader((Header) items.get(position));
        } else if (type == ItemType.BEER) {
            ((BeerViewHolder) holder).bind((BeerViewModel) items.get(position));
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof BeerViewHolder) {
            ((BeerViewHolder) holder).unbind();
        }

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);

        if (item instanceof Header) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.BEER.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void set(@NonNull List<BeerViewModel> beers) {
        checkNotNull(beers);

        if (!beers.equals(this.beers)) {
            this.beers.clear();
            this.beers.addAll(beers);

            recreateList();
        }
    }
}
