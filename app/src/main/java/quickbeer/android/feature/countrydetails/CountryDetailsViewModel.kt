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
package quickbeer.android.feature.countrydetails

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
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeersInCountryRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingMapper

@HiltViewModel
class CountryDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val countryRepository: CountryRepository,
    private val beersInCountryRepository: BeersInCountryRepository,
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val countryId = savedStateHandle.get<Int>(NavParams.ID)!!

    private val _countryState = MutableStateFlow<State<Country>>(State.Initial)
    val countryState: Flow<State<Country>> = _countryState

    private val _beersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beersState: Flow<State<List<BeerListModel>>> = _beersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            countryRepository.getStream(countryId, Accept())
                .collectLatest(_countryState::emit)
        }

        viewModelScope.launch(Dispatchers.IO) {
            beersInCountryRepository.getStream(countryId.toString(), Accept())
                .map(BeerListModelRatingMapper(beerRepository)::map)
                .collectLatest(_beersState::emit)
        }
    }
}
