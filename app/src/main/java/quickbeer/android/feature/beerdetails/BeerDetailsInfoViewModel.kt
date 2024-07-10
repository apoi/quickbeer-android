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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import quickbeer.android.data.state.State
import quickbeer.android.domain.ratinglist.repository.UserAllRatingsRepository
import quickbeer.android.feature.beerdetails.model.BeerDetailsState
import quickbeer.android.feature.beerdetails.usecase.GetBeerDetailsUseCase
import quickbeer.android.util.ktx.navId
import timber.log.Timber

@HiltViewModel
class BeerDetailsInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBeerDetailsUseCase: GetBeerDetailsUseCase,
    private val userAllRatingsRepository: UserAllRatingsRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()
    private val isTicking = MutableStateFlow(false)

    private val _viewState = MutableStateFlow<State<BeerDetailsState>>(State.Initial)
    val viewState: StateFlow<State<BeerDetailsState>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getBeerDetailsUseCase.getBeerDetails(beerId)
                .combine(isTicking) { state, showTickView ->
                    Timber.d("STATE $state")
                    if (!showTickView) {
                        state
                    } else {
                        // Always show tick view if user chose the action
                        state.map { it.copy(tick = it.tick.forceShow()) }
                    }
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
            // TODO()
            /*
            val beer = beerRepository.store.get(beerId) ?: error("No beer found!")
            val userId = loginManager.userId.first() ?: error("Not logged in!")
            val fetchKey = BeerTickFetcher.TickKey(beerId, userId, tick)
            val result = beerTickFetcher.fetch(fetchKey)

            if (result is ApiResult.Success) {
                val update = beer.copy(tickValue = tick, tickDate = ZonedDateTime.now())
                beerRepository.persist(beerId, update)
            }


            val message = when {
                result !is ApiResult.Success -> resourceProvider.getString(R.string.tick_failure)
                tick > 0 -> resourceProvider.getString(R.string.tick_success).format(beer.name)
                else -> resourceProvider.getString(R.string.tick_removed)
            }


            withContext(Dispatchers.Main) {
                toastProvider.showToast(message)
            }
             */
        }
    }

    fun showTickCard() {
        viewModelScope.launch(Dispatchers.IO) {
            isTicking.emit(true)
        }
    }
}
