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

import android.view.View
import android.widget.TextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.beer_list_item.view.*
import quickbeer.android.R
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.core.viewmodel.viewholder.BindingViewHolder
import quickbeer.android.data.pojos.Beer
import quickbeer.android.utils.Score
import quickbeer.android.utils.StringUtils
import quickbeer.android.viewmodels.BeerViewModel
import timber.log.Timber

class BeerViewHolder(view: View, onClickListener: View.OnClickListener) : BindingViewHolder<BeerViewModel>(view) {

    private val ratingTextView: TextView = view.beer_stars
    private val nameTextView: TextView = view.beer_name
    private val styleTextView: TextView = view.beer_style
    private val brewerTextView: TextView = view.brewer_name

    init {
        view.setOnClickListener(onClickListener)
    }

    override val viewDataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            clearViews()

            disposable.add(getViewModel()!!
                .getBeer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setBeer(it) }, { Timber.e(it) }))
        }
    }

    private fun setBeer(beer: Beer) {
        // Don't show anything if the name isn't loaded yet.
        // This prevents the rating label to be shown with empty details.
        if (StringUtils.isEmpty(beer.name)) {
            return
        }

        if (beer.isTicked()) {
            ratingTextView.text = ""
            ratingTextView.setBackgroundResource(Score.fromTick(beer.tickValue).resource)
        } else {
            ratingTextView.text = Score.fromRating(beer.rating())
            ratingTextView.setBackgroundResource(R.drawable.score_unrated)
        }

        nameTextView.text = beer.name
        styleTextView.text = beer.styleName
        brewerTextView.text = beer.brewerName
    }

    private fun clearViews() {
        ratingTextView.background = null
        ratingTextView.text = ""
        nameTextView.text = ""
        styleTextView.text = ""
        brewerTextView.text = ""
    }
}
