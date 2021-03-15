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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.reviewlist.repository.BeerReviewsRepository
import quickbeer.android.ui.adapter.review.ReviewListModel
import quickbeer.android.ui.adapter.review.ReviewListModelUpdatedDateMapper
import timber.log.Timber

class BeerReviewsViewModel(
    private val beerId: Int,
    private val reviewRepository: BeerReviewsRepository
) : ViewModel() {

    private val _reviewsState = MutableLiveData<State<List<ReviewListModel>>>()
    val reviewsState: LiveData<State<List<ReviewListModel>>> = _reviewsState

    init {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        Timber.w("Load page $page")

        viewModelScope.launch(Dispatchers.IO) {
            reviewRepository
                .getStream(beerId.toString(), Accept())
                .map(ReviewListModelUpdatedDateMapper()::map)
                .collect { _reviewsState.postValue(it) }
        }
    }
}
