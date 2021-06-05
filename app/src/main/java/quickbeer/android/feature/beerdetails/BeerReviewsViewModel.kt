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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.reviewlist.repository.BeerReviewsRepository
import quickbeer.android.ui.adapter.review.ReviewListModel
import quickbeer.android.ui.adapter.review.ReviewListModelDateSortingMapper
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerReviewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val reviewRepository: BeerReviewsRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()

    private val _reviewsState = MutableStateFlow<State<List<ReviewListModel>>>(State.Initial)
    val reviewsState: Flow<State<List<ReviewListModel>>> = _reviewsState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            reviewRepository
                .getStream(beerId.toString(), NoFetch())
                .map(ReviewListModelDateSortingMapper()::map)
                .collectLatest(_reviewsState::emit)
        }
    }

    fun loadInitialReviews() {
        val state = _reviewsState.value
        if (state !is State.Success || state.value.isEmpty()) {
            loadReviewPage(0)
        }
    }

    fun loadReviewPage(itemIndex: Int) {
        if (itemIndex % ITEMS_ON_PAGE != 0) return
        val page = itemIndex / ITEMS_ON_PAGE + 1

        viewModelScope.launch(Dispatchers.IO) {
            reviewRepository
                .fetch(beerId.toString(), page)
        }
    }

    companion object {
        const val ITEMS_ON_PAGE = 10
    }
}
