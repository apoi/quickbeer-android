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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.viewholders.BeerViewHolder;
import quickbeer.android.next.views.viewholders.HeaderViewHolder;

public class BeerListAdapter extends BaseListAdapter {
    private final List<BeerViewModel> beers = new ArrayList<>();
    private View.OnClickListener onClickListener;

    public BeerListAdapter(List<BeerViewModel> beers) {
        this.beers.addAll(beers);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BeerViewModel getItem(int position) {
        return beers.get(position - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.ordinal()) {
            View v = new View(parent.getContext());
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_list_item, parent, false);
            return new BeerViewHolder(v, onClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        if (type == ItemType.BEER) {
            ((BeerViewHolder) holder).bind(getItem(position));
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
        if (position == 0) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.BEER.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return beers.size() + 1;
    }

    public void set(List<BeerViewModel> beers) {
        if (!beers.equals(this.beers)) {
            this.beers.clear();
            this.beers.addAll(beers);

            notifyDataSetChanged();
        }
    }
}
