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
import quickbeer.android.domain.beerlist.repository.BrewersBeersRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelAlphabeticalMapper
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BrewerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val brewerRepository: BrewerRepository,
    private val brewersBeersRepository: BrewersBeersRepository,
    private val countryRepository: CountryRepository,
    private val beerListMapper: BeerListModelAlphabeticalMapper
) : ViewModel() {

    private val brewerId = savedStateHandle.navId()

    private val _brewerState = MutableStateFlow<State<Brewer>>(State.Initial)
    val brewerState: StateFlow<State<Brewer>> = _brewerState

    private val _beersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beersState: StateFlow<State<List<BeerListModel>>> = _beersState

    private val _addressState = MutableStateFlow<State<Address>>(State.Initial)
    val addressState: StateFlow<State<Address>> = _addressState

    init {
        updateAccessedBrewer(brewerId)

        viewModelScope.launch(Dispatchers.IO) {
            brewerRepository.getStream(brewerId, Brewer.DetailsDataValidator())
                .collect {
                    _brewerState.emit(it)
                    if (it is State.Success) getAddress(it.value)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            brewersBeersRepository.getStream(brewerId.toString(), Accept())
                .map(beerListMapper::map)
                .collectLatest(_beersState::emit)
        }
    }

    private fun updateAccessedBrewer(brewerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            brewerRepository.getStream(brewerId, Accept())
                .filterIsInstance<State.Success<Brewer>>()
                .map { it.value }
                .take(1)
                .collect { brewer ->
                    val accessed = brewer.copy(accessed = ZonedDateTime.now())
                    brewerRepository.persist(brewer.id, accessed)
                }
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
        } else {
            State.Loading()
        }
    }
}
