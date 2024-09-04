/**
 * This file is part of QuickBeer.
 * Copyright (C) 2024 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.feature.placedetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.repository.PlaceRepository
import quickbeer.android.feature.placedetails.model.PlaceDetailsState
import quickbeer.android.usecase.GetAddressUseCase
import quickbeer.android.util.ktx.navId

@HiltViewModel
class PlaceDetailsInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
    private val brewerRepository: BrewerRepository,
    private val countryRepository: CountryRepository,
    private val getAddressUseCase: GetAddressUseCase
) : ViewModel() {

    private val placeId = savedStateHandle.navId()

    private val _viewState = MutableStateFlow<State<PlaceDetailsState>>(State.Initial)
    val viewState: StateFlow<State<PlaceDetailsState>> = _viewState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getViewState()
                .collectLatest(_viewState::emit)
        }
    }

    private fun getViewState(): Flow<State<PlaceDetailsState>> {
        val placeFlow = placeRepository
            .getStream(placeId, Place.DetailsDataValidator())
            .distinctUntilChanged()

        val brewerFlow = getBrewer(placeFlow)
        val countryFlow = getCountry(placeFlow)
        val addressFlow = getAddressUseCase.getPlaceAddress(placeFlow, countryFlow)

        return combine(placeFlow, brewerFlow, addressFlow) { place, brewer, address ->
            val v = PlaceDetailsState.create(place, brewer, address)
            if (v != null) State.from(v) else State.Initial
        }
    }

    private fun getBrewer(placeFlow: Flow<State<Place>>): Flow<State<Brewer>> {
        return placeFlow
            .mapNotNull { it.valueOrNull()?.brewerId }
            .flatMapLatest { brewerId ->
                brewerRepository.getStream(brewerId, Brewer.DetailsDataValidator())
            }
            .onStart { emit(State.Initial) }
    }

    private fun getCountry(placeFlow: Flow<State<Place>>): Flow<State<Country>> {
        return placeFlow
            .mapNotNull { it.valueOrNull()?.brewerId }
            .flatMapLatest { brewerId ->
                countryRepository.getStream(brewerId, Accept())
            }
            .onStart { emit(State.Initial) }
    }
}
