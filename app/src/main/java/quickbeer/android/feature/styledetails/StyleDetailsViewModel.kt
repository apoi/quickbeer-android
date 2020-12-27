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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.data.state.StateMapper
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.beerlist.repository.BeersInStyleRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.feature.shared.adapter.BeerListModel

class StyleDetailsViewModel(
    styleId: Int,
    private val styleRepository: StyleRepository,
    private val beerRepository: BeersInStyleRepository,
    private val beerStore: BeerRepository
) : ViewModel() {

    private val _styleState = MutableLiveData<State<Style>>()
    val styleState: LiveData<State<Style>> = _styleState

    private val _beersState = MutableLiveData<State<List<BeerListModel>>>()
    val beersState: LiveData<State<List<BeerListModel>>> = _beersState

    private val _parentStyleState = MutableLiveData<State<Style>>()
    val parentStyleState: LiveData<State<Style>> = _parentStyleState

    init {
        viewModelScope.launch {
            styleRepository.getStream(styleId, Accept())
                .collect {
                    if (it is State.Success) getParentStyle(it.value.parent)
                    _styleState.postValue(it)
                }
        }

        viewModelScope.launch {
            beerRepository.getStream(styleId.toString(), Accept())
                .map(beerRatingSorter()::map)
                .collect { _beersState.postValue(it) }
        }
    }

    private fun beerRatingSorter(): StateMapper<List<Beer>, List<BeerListModel>> {
        return StateMapper { list ->
            list.sortedByDescending(Beer::averageRating)
                .map { BeerListModel(it.id, beerStore) }
        }
    }

    private fun getParentStyle(parentStyleId: Int?) {
        if (parentStyleId == null) return

        viewModelScope.launch {
            styleRepository.getStream(parentStyleId, Accept())
                .collect { _parentStyleState.postValue(it) }
        }
    }
}
