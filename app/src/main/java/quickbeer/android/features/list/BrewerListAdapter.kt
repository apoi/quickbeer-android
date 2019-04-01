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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
import quickbeer.android.adapters.BaseListAdapter
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.utils.kotlin.contentsEqual
import quickbeer.android.viewmodels.BrewerViewModel
import quickbeer.android.views.viewholders.BrewerViewHolder
import java.util.ArrayList

class BrewerListAdapter(
    private val countryStore: CountryStore,
    private val onClickListener: View.OnClickListener
) : BaseListAdapter() {

    private val brewers = ArrayList<BrewerViewModel>(10)

    fun getBrewerViewModel(position: Int): BrewerViewModel {
        return brewers[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.brewer_list_item, parent, false)
        return BrewerViewHolder(view, countryStore, onClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BrewerViewHolder).bind(brewers[position])
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        (holder as? BrewerViewHolder)?.unbind()

        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return brewers.size
    }

    fun set(brewers: List<BrewerViewModel>) {
        if (!this.brewers.contentsEqual(brewers)) {
            this.brewers.clear()
            this.brewers.addAll(brewers)

            notifyDataSetChanged()
        }
    }
}
