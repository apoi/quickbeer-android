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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.feature.beerdetails.model.BeerDetailsState
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBeerDetailsUseCase: GetBeerDetailsUseCase,
    private val beerRepository: BeerRepository,
    private val brewerRepository: BrewerRepository,
    private val userRatingRepository: UserRatingRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()

    private val _viewState = MutableStateFlow<State<BeerDetailsState>>(State.Initial)
    val viewState: Flow<State<BeerDetailsState>> = _viewState

    init {
        updateAccessedBeer(beerId)
        updateAccessedBrewer(beerId)

        viewModelScope.launch(Dispatchers.IO) {
            getBeerDetailsUseCase.getBeerDetails(beerId)
                .collectLatest {
                    _viewState.emit(it)
                }
        }
    }

    fun deleteRating(ratingId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            userRatingRepository.delete(ratingId)
        }
    }

    private fun updateAccessedBeer(beerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            beerRepository.getStream(beerId, Accept())
                .filterIsInstance<State.Success<Beer>>()
                .map { it.value }
                .take(1)
                .collectLatest { beer ->
                    val accessed = beer.copy(accessed = ZonedDateTime.now())
                    beerRepository.persist(beer.id, accessed)
                }
        }
    }

    private fun updateAccessedBrewer(beerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            beerRepository.getStream(beerId, Accept())
                .mapNotNull { it.valueOrNull()?.brewerId }
                .distinctUntilChanged()
                .flatMapLatest { brewerId ->
                    brewerRepository.getStream(brewerId, Accept())
                        .filterIsInstance<State.Success<Brewer>>()
                        .map { it.value }
                        .take(1)
                }
                .collectLatest { brewer ->
                    val accessed = brewer.copy(accessed = ZonedDateTime.now())
                    brewerRepository.persist(brewer.id, accessed)
                }
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
}
