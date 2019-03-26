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

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.ViewModel

abstract class BindingViewHolder<T : ViewModel> protected constructor(view: View) : RecyclerView.ViewHolder(view) {

    protected abstract val viewDataBinder: DataBinder

    private var viewModel: T? = null

    private val disposable = CompositeDisposable()

    fun bind(viewModel: T) {
        setAndBindDataModel(viewModel)
        bindViewToViewModel()
    }

    fun unbind() {
        unbindViewFromViewModel()
        unbindViewModelFromData()
    }

    private fun bindViewToViewModel() {
        viewDataBinder.bind(disposable)
    }

    protected fun getViewModel(): T? {
        return viewModel
    }

    private fun setAndBindDataModel(viewModel: T) {
        this.viewModel = viewModel
        viewModel.bindToDataModel()
    }

    private fun unbindViewFromViewModel() {
        // Don't dispose - we need to reuse it when recycling!
        disposable.clear()
        viewDataBinder.unbind()
    }

    private fun unbindViewModelFromData() {
        viewModel?.unbindDataModel()
        viewModel = null
    }
}
