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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.repository.PlaceRepository
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.util.ktx.navId

@HiltViewModel
class PlaceDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val placeRepository: PlaceRepository,
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val placeId = savedStateHandle.navId()

    private val _placeState = MutableStateFlow<State<Place>>(State.Initial)
    val placeState: StateFlow<State<Place>> = _placeState

    private val _addressState = MutableStateFlow<State<Address>>(State.Initial)
    val addressState: StateFlow<State<Address>> = _addressState

    init {
        updateAccessedPlace(placeId)

        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.getStream(placeId, Place.DetailsDataValidator())
                .collectLatest {
                    _placeState.emit(it)
                    if (it is State.Success) getAddress(it.value)
                }
        }
    }

    private fun updateAccessedPlace(placeId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            placeRepository.getStream(placeId, Accept())
                .filterIsInstance<State.Success<Place>>()
                .map { it.value }
                .take(1)
                .collectLatest { place ->
                    val accessed = place.copy(accessed = ZonedDateTime.now())
                    placeRepository.persist(place.id, accessed)
                }
        }
    }

    private fun getAddress(place: Place) {
        if (place.countryId == null) return

        viewModelScope.launch(Dispatchers.IO) {
            countryRepository.getStream(place.countryId, Accept())
                .map { mergeAddress(place, it) }
                .collectLatest(_addressState::emit)
        }
    }

    private fun mergeAddress(place: Place, country: State<Country>): State<Address> {
        return if (country is State.Success) {
            State.Success(Address.from(place, country.value))
        } else {
            State.Loading()
        }
    }
}
