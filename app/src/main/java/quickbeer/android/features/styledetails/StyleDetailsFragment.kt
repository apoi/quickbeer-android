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
package quickbeer.android.features.styledetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.style_details_fragment_details.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.injections.IdModule
import quickbeer.android.utils.kotlin.filterToValue
import timber.log.Timber
import javax.inject.Inject

class StyleDetailsFragment : BindingBaseFragment() {

    @Inject
    lateinit var viewModel: StyleViewModel

    private var styleId: Int = 0

    companion object {
        fun newInstance(styleId: Int): Fragment {
            val bundle = Bundle()
            bundle.putInt(Constants.ID_KEY, styleId)
            val fragment = StyleDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            disposable.add(viewModel()
                .getStyle()
                .toObservable()
                .filterToValue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ style_details_view.setStyle(it) }, { Timber.e(it) }))

            disposable.add(viewModel()
                .getParentStyle()
                .toObservable()
                .filterToValue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ style_details_view.setParent(it) }, { Timber.e(it) }))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = savedInstanceState ?: arguments
        styleId = bundle?.getInt(Constants.ID_KEY) ?: 0

        if (styleId == 0) {
            Timber.w("Expected state for initializing!")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(Constants.ID_KEY, styleId)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.style_details_fragment_details, container, false)
    }

    override fun viewModel(): StyleViewModel {
        return viewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun inject() {
        getComponent().plusId(IdModule(styleId))
            .inject(this)
    }
}
