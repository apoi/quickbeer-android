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
import kotlinx.android.synthetic.main.brewer_list_item.view.*
import polanski.option.Option.ofObj
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.core.viewmodel.viewholder.BindingViewHolder
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.utils.kotlin.hasValue
import quickbeer.android.viewmodels.BrewerViewModel
import timber.log.Timber

class BrewerViewHolder(
    view: View,
    private val countryStore: CountryStore,
    onClickListener: View.OnClickListener
) : BindingViewHolder<BrewerViewModel>(view) {

    private val brewerCircle: TextView = view.brewer_origin
    private val brewerName: TextView = view.brewer_name
    private val brewerCountry: TextView = view.brewer_country

    init {
        view.setOnClickListener(onClickListener)
    }

    override val viewDataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            clearViews()

            disposable.add(
                getViewModel()!!
                    .getBrewer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ setBrewer(it) }, { Timber.e(it) }))
        }
    }

    private fun setBrewer(brewer: Brewer) {
        brewerName.text = brewer.name

        val countryOption = ofObj(brewer.countryId)
            .map { countryStore.getItem(it) }

        countryOption.ifSome { (_, _, code) -> brewerCircle.text = code }

        ofObj(brewer.city)
            .filter { it.hasValue() }
            .lift(countryOption) { city, (_, name) -> String.format("%s, %s", city, name) }
            .orOption { countryOption.map { it.name } }
            .orOption { ofObj("Unknown") }
            .ifSome { brewerCountry.text = it }
    }

    private fun clearViews() {
        brewerCircle.text = ""
        brewerName.text = ""
        brewerCountry.text = ""
    }
}
