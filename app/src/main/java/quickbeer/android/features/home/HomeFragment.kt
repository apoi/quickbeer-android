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
package quickbeer.android.features.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.barcode.Barcode
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_fragment_pager.*
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Screen
import quickbeer.android.core.fragment.BindingBaseFragment
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.features.barcodescanner.BarcodeScanActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.NavigationProvider.Page
import quickbeer.android.viewmodels.SearchViewViewModel
import quickbeer.android.viewmodels.SearchViewViewModel.Mode
import timber.log.Timber
import javax.inject.Inject

class HomeFragment : BindingBaseFragment() {

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    @Inject
    internal lateinit var analytics: Analytics

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            disposable.add(
                viewModel().getQueryStream()
                    .subscribe({ navigationProvider.triggerSearch(it) }, { Timber.e(it) }))
        }
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment_pager, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view_pager.adapter = HomeViewAdapter(childFragmentManager, context!!)
        tab_layout.setupWithViewPager(view_pager)

        view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val screen = if (position == 0)
                    Screen.HOME_BEERS
                else
                    Screen.HOME_BREWERS
                analytics.createEvent(screen)
            }
        })

        barcode_scan_fab.setOnClickListener {
            val intent = Intent(activity, BarcodeScanActivity::class.java)
            startActivityForResult(intent, CODE_CAPTURE_BARCODE)
        }

        viewModel().setMode(Mode.SEARCH, getString(R.string.search_box_hint_search_beers))
    }

    override fun viewModel(): SearchViewViewModel {
        return searchViewViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != CODE_CAPTURE_BARCODE) {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (resultCode != CommonStatusCodes.SUCCESS) {
            Timber.e("Failure capturing barcode: %s", CommonStatusCodes.getStatusCodeString(resultCode))
            return
        }

        if (data == null) {
            Timber.e("No barcode captured, intent data is null")
            return
        }

        val barcode = data.getParcelableExtra<Barcode>(BarcodeScanActivity.BARCODE_RESULT)
        Timber.d("Barcode found: " + barcode.displayValue)

        val bundle = Bundle()
        bundle.putString("barcode", barcode.displayValue)

        navigationProvider.addPage(Page.BARCODE_SEARCH, bundle)
    }

    companion object {
        private val CODE_CAPTURE_BARCODE = 9876
    }
}
