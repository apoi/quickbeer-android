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
package quickbeer.android.feature.styledetails

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
import quickbeer.android.domain.beerlist.repository.BeersInStyleRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.ui.adapter.beer.BeerListModel
import quickbeer.android.ui.adapter.beer.BeerListModelRatingMapper
import quickbeer.android.util.ktx.navId

@HiltViewModel
class StyleDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val styleRepository: StyleRepository,
    private val beersInStyleRepository: BeersInStyleRepository,
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val styleId = savedStateHandle.navId()

    private val _styleState = MutableStateFlow<State<Style>>(State.Initial)
    val styleState: Flow<State<Style>> = _styleState

    private val _beersState = MutableStateFlow<State<List<BeerListModel>>>(State.Initial)
    val beersState: Flow<State<List<BeerListModel>>> = _beersState

    private val _parentStyleState = MutableStateFlow<State<Style>>(State.Initial)
    val parentStyleState: Flow<State<Style>> = _parentStyleState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            styleRepository.getStream(styleId, Accept())
                .collect {
                    if (it is State.Success) getParentStyle(it.value.parent)
                    _styleState.emit(it)
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            beersInStyleRepository.getStream(styleId.toString(), Accept())
                .map(BeerListModelRatingMapper(beerRepository)::map)
                .collectLatest(_beersState::emit)
        }
    }

    private fun getParentStyle(parentStyleId: Int?) {
        if (parentStyleId == null) return

        viewModelScope.launch(Dispatchers.IO) {
            styleRepository.getStream(parentStyleId, Accept())
                .collectLatest(_parentStyleState::emit)
        }
    }
}
