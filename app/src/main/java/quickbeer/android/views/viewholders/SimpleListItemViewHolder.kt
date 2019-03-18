/*
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
package quickbeer.android.views.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.simple_list_item.view.*
import quickbeer.android.data.pojos.SimpleItem

class SimpleListItemViewHolder(view: View, onClickListener: View.OnClickListener) : RecyclerView.ViewHolder(view) {

    private val textView: TextView = view.list_item_name
    private val iconView: TextView = view.list_item_icon

    init {
        view.setOnClickListener(onClickListener)
    }

    fun setItem(simpleItem: SimpleItem) {
        iconView.text = simpleItem.code
        textView.text = simpleItem.name
    }
}
