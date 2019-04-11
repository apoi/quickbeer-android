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
package quickbeer.android.core.viewmodel

import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

abstract class BaseViewModel : ViewModel {

    protected val disposable = CompositeDisposable()

    private var isBound: Boolean = false

    override fun bindToDataModel() {
        if (!isBound) {
            bind(disposable)
            isBound = true
        }
    }

    override fun unbindDataModel() {
        if (isBound) {
            disposable.clear()
            unbind()
            isBound = false
        }
    }

    protected abstract fun bind(disposable: CompositeDisposable)

    protected abstract fun unbind()

    override fun dispose() {
        if (isBound) {
            Timber.e("Disposing before unbind!")
            unbindDataModel()
        }
    }
}
