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
package quickbeer.android.feature.beerdetails

import quickbeer.android.databinding.RatingListItemBinding
import quickbeer.android.domain.rating.RatingBinder
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.ui.adapter.rating.RatingListModel

/**
 * View holder for ratings in list
 */
class BeerRatingsViewHolder(
    private val binding: RatingListItemBinding
) : ListViewHolder<RatingListModel>(binding.root) {

    override fun bind(item: RatingListModel) {
        RatingBinder.bind(itemView.context, item.rating, binding)
    }
}
