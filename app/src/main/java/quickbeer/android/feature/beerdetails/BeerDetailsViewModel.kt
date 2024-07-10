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
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val beerRepository: BeerRepository,
    private val brewerRepository: BrewerRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()

    private val _beerState = MutableStateFlow<State<Beer>>(State.Initial)
    val beerState: StateFlow<State<Beer>> = _beerState

    init {
        updateAccessedBeer(beerId)
        updateAccessedBrewer(beerId)

        viewModelScope.launch(Dispatchers.IO) {
            beerRepository.getStream(beerId, Accept())
                .collectLatest(_beerState::emit)
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
}
