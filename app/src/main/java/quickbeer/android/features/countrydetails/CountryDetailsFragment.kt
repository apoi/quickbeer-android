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
package quickbeer.android.features.countrydetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.country_details_fragment_details.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.injections.IdModule
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class CountryDetailsFragment : BindingBaseFragment() {

    @Inject
    lateinit var viewModel: CountryViewModel

    private var countryId: Int = 0

    companion object {
        fun newInstance(countryId: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, countryId)
            val fragment = CountryDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            disposable.add(
                viewModel()
                    .getCountry()
                    .toObservable()
                    .filterToValue()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ country_details_view.setCountry(it) }, { Timber.e(it) }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = savedInstanceState ?: arguments
        countryId = bundle?.getInt(Constants.ID_KEY) ?: 0

        if (countryId == 0) {
            Timber.w("Expected state for initializing!")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, countryId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.country_details_fragment_details, container, false)
    }

    override fun viewModel(): CountryViewModel {
        return viewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun inject() {
        getComponent().plusId(IdModule(countryId))
            .inject(this)
    }
}
