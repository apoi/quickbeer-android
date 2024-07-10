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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import quickbeer.android.R
import quickbeer.android.data.result.Result
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.ratinglist.repository.UserAllRatingsRepository
import quickbeer.android.feature.beerdetails.model.BeerDetailsInfoViewEvent
import quickbeer.android.feature.beerdetails.model.BeerDetailsState
import quickbeer.android.feature.beerdetails.model.TickActionState
import quickbeer.android.feature.beerdetails.model.TickActionState.ActionClicked
import quickbeer.android.feature.beerdetails.model.TickActionState.Default
import quickbeer.android.feature.beerdetails.model.TickActionState.LoadingInProgress
import quickbeer.android.feature.beerdetails.usecase.GetBeerDetailsUseCase
import quickbeer.android.feature.beerdetails.usecase.TickBeerUseCase
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.SingleLiveEvent
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerDetailsInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val beerRepository: BeerRepository,
    private val tickBeerUseCase: TickBeerUseCase,
    private val getBeerDetailsUseCase: GetBeerDetailsUseCase,
    private val userAllRatingsRepository: UserAllRatingsRepository,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val beerId = savedStateHandle.navId()
    private val tickActionState = MutableStateFlow<TickActionState>(Default)

    private val _viewState = MutableStateFlow<State<BeerDetailsState>>(State.Initial)
    val viewState: StateFlow<State<BeerDetailsState>> = _viewState

    private val _events = SingleLiveEvent<BeerDetailsInfoViewEvent>()
    val events: LiveData<BeerDetailsInfoViewEvent> = _events

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getBeerDetailsUseCase.getBeerDetails(beerId)
                .combine(tickActionState) { state, tickActionState ->
                    state.map { it.copy(tickActionState = tickActionState) }
                }
                .collectLatest {
                    _viewState.emit(it)
                }
        }
    }

    fun deleteRating(ratingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userAllRatingsRepository.delete(ratingId)
        }
    }

    fun tickBeer(tick: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tickActionState.emit(LoadingInProgress)

            val beer = beerRepository.get(beerId) ?: error("Invalid beer id")
            val result = tickBeerUseCase.tickBeer(beerId, tick)

            val message = when {
                result !is Result.Success -> resourceProvider.getString(R.string.tick_failure)
                tick > 0 -> resourceProvider.getString(R.string.tick_success).format(beer.name)
                else -> resourceProvider.getString(R.string.tick_removed)
            }

            val actionState = when (result) {
                is Result.Success -> Default
                is Result.Failure -> ActionClicked
            }

            tickActionState.emit(actionState)
            _events.postValue(BeerDetailsInfoViewEvent.ShowMessage(message))
        }
    }

    fun createTickClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            tickActionState.emit(ActionClicked)
        }
    }
}
