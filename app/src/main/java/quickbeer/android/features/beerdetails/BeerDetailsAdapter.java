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
package quickbeer.android.features.beerdetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import quickbeer.android.R;
import quickbeer.android.adapters.BaseListAdapter;
import quickbeer.android.data.pojos.Beer;

public class BeerDetailsAdapter extends BaseListAdapter {

    private Beer beer;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_view, parent, false);
        return new BeerDetailsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BeerDetailsViewHolder) holder).setBeer(beer);
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.BEER.ordinal();
    }

    @Override
    public int getItemCount() {
        return beer != null ? 1 : 0;
    }

    public void setBeer(Beer beer) {
        if (!beer.equals(this.beer)) {
            this.beer = beer;

            notifyDataSetChanged();
        }
    }
}
