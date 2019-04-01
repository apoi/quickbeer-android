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
package quickbeer.android.adapters

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import polanski.option.Option
import polanski.option.Option.none
import polanski.option.Option.ofObj
import quickbeer.android.R
import quickbeer.android.data.pojos.SimpleItem
import quickbeer.android.views.viewholders.SimpleListItemViewHolder
import timber.log.Timber
import java.util.ArrayList
import java.util.Collections

class SimpleListAdapter(
    countries: Collection<SimpleItem>,
    private val onClickListener: View.OnClickListener
) : BaseListAdapter() {

    private val sourceList: List<SimpleItem>

    private val adapterList = ArrayList<SimpleItem>(0)

    init {
        sourceList = ArrayList(countries)
        Collections.sort(sourceList)

        adapterList.addAll(sourceList)
    }

    fun filterList(filter: String) {
        Timber.v("filter($filter)")

        adapterList.clear()

        if (TextUtils.isEmpty(filter)) {
            adapterList.addAll(sourceList)
        } else {
            for (item in sourceList) {
                if (item.name.toLowerCase().contains(filter.toLowerCase())) {
                    adapterList.add(item)
                }
            }
        }

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.simple_list_item, parent, false)
        return SimpleListItemViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SimpleListItemViewHolder).setItem(adapterList[position])
    }

    fun getItemAt(index: Int): Option<SimpleItem> {
        return if (index >= itemCount || index < 0) {
            none()
        } else {
            ofObj(adapterList[index])
        }
    }

    override fun getItemCount(): Int {
        return adapterList.size
    }
}
