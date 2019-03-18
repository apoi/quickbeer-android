/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.features.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.adapters.BaseListAdapter
import quickbeer.android.utils.kotlin.contentsEqual
import quickbeer.android.viewmodels.BeerViewModel
import quickbeer.android.views.viewholders.BeerViewHolder
import java.util.ArrayList

class BeerListAdapter(private val onClickListener: View.OnClickListener) : BaseListAdapter() {

    private val beers = ArrayList<BeerViewModel>(10)

    fun getBeerViewModel(position: Int): BeerViewModel {
        return beers[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.beer_list_item, parent, false)
        return BeerViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BeerViewHolder).bind(beers[position])
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        (holder as? BeerViewHolder)?.unbind()

        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return beers.size
    }

    fun set(beers: List<BeerViewModel>) {
        if (!this.beers.contentsEqual(beers)) {
            this.beers.clear()
            this.beers.addAll(beers)

            notifyDataSetChanged()
        }
    }
}
