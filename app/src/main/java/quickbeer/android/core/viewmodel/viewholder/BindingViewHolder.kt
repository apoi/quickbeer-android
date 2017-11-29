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
package quickbeer.android.core.viewmodel.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import io.reark.reark.utils.Preconditions.checkNotNull
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.ViewModel
import rx.subscriptions.CompositeSubscription

abstract class BindingViewHolder<T : ViewModel> protected constructor(view: View)
    : RecyclerView.ViewHolder(view) {

    protected abstract val viewDataBinder: DataBinder

    protected var viewModel: T? = null
        private set

    private val subscription = CompositeSubscription()

    fun bind(viewModel: T) {
        setAndBindDataModel(viewModel)
        bindViewToViewModel()
    }

    fun unbind() {
        unbindViewFromViewModel()
        unbindViewModelFromData()
    }

    private fun bindViewToViewModel() {
        viewDataBinder.bind(subscription)
    }

    private fun setAndBindDataModel(viewModel: T) {
        this.viewModel = viewModel
        viewModel.bindToDataModel()
    }

    private fun unbindViewFromViewModel() {
        // Don't dispose - we need to reuse it when recycling!
        subscription.clear()
        viewDataBinder.unbind()
    }

    private fun unbindViewModelFromData() {
        checkNotNull(viewModel, "View Model cannot be null when unbinding")
        viewModel!!.unbindDataModel()
        viewModel = null
    }
}