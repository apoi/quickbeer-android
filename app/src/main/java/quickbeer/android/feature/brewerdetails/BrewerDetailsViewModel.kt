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
package quickbeer.android.feature.brewerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BrewersBeersRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelAlphabeticMapper

@HiltViewModel
class BrewerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val brewerRepository: BrewerRepository,
    private val brewersBeersRepository: BrewersBeersRepository,
    private val beerRepository: BeerRepository,
    private val countryRepository: CountryRepository
) : ViewModel() {

    private val brewerId = savedStateHandle.get<Int>(NavParams.ID)!!

    private val _brewerState = MutableStateFlow<State<Brewer>>(State.Initial)
    val brewerState: Flow<State<Brewer>> = _brewerState

    private val _beersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beersState: Flow<State<List<BeerListModel>>> = _beersState

    private val _addressState = MutableStateFlow<State<Address>>(State.Initial)
    val addressState: Flow<State<Address>> = _addressState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            brewerRepository.getStream(brewerId, Brewer.DetailsDataValidator())
                .collect {
                    _brewerState.emit(it)
                    if (it is State.Success) getAddress(it.value)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            brewersBeersRepository.getStream(brewerId.toString(), Accept())
                .map(BeerListModelAlphabeticMapper(beerRepository)::map)
                .collectLatest(_beersState::emit)
        }
    }

    private fun getAddress(brewer: Brewer) {
        if (brewer.countryId == null) return

        viewModelScope.launch(Dispatchers.IO) {
            countryRepository.getStream(brewer.countryId, Accept())
                .map { mergeAddress(brewer, it) }
                .collectLatest(_addressState::emit)
        }
    }

    private fun mergeAddress(brewer: Brewer, country: State<Country>): State<Address> {
        return if (country is State.Success) {
            State.Success(Address.from(brewer, country.value))
        } else State.Loading()
    }
}
