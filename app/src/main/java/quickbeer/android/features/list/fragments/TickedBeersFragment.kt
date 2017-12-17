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
package quickbeer.android.features.list.fragments

import android.content.Intent
import android.support.v7.app.AlertDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import quickbeer.android.R
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.data.pojos.Session
import quickbeer.android.features.profile.ProfileActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.utils.kotlin.filterToValue
import quickbeer.android.viewmodels.NetworkViewModel
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus
import quickbeer.android.viewmodels.TickedBeersViewModel
import timber.log.Timber
import javax.inject.Inject

class TickedBeersFragment : BeerListFragment() {

    @Inject
    internal lateinit var tickedBeersViewModel: TickedBeersViewModel

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var session: Session

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            super@TickedBeersFragment.dataBinder().bind(disposable);

            disposable.add(viewModel()
                    .getUser()
                    .take(1)
                    .filter { it.isNone() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ showLoginDialog() }, { Timber.e(it) }))

            disposable.add(viewModel()
                    .getUser()
                    .take(1)
                    .filterToValue()
                    .filter { !session.isTicksRequested }
                    .doOnNext { session.isTicksRequested = true }
                    .subscribe({ viewModel().refreshTicks(it) }, { Timber.e(it) }))
        }

        override fun unbind() {
            super@TickedBeersFragment.dataBinder().unbind()
        }
    }

    override fun inject() {
        super.inject()

        getComponent().inject(this)
    }

    override fun toStatusValue(progressStatus: ProgressStatus): String {
        return if (progressStatus === NetworkViewModel.ProgressStatus.EMPTY)
            getString(R.string.ticks_empty_list)
        else
            super.toStatusValue(progressStatus)
    }

    private fun showLoginDialog() {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.login_dialog_title)
                .setMessage(R.string.login_to_view_ratings_message)
                .setPositiveButton(R.string.ok) { _, _ -> navigateToLogin() }
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                .show()
    }

    private fun navigateToLogin() {
        Timber.d("navigateToLogin")

        val intent = Intent(context, ProfileActivity::class.java)
        context!!.startActivity(intent)
    }

    override fun viewModel(): TickedBeersViewModel {
        return tickedBeersViewModel
    }

    override fun onQuery(query: String) {
        navigationProvider.triggerSearch(query)
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }
}
