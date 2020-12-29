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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.domain.stylelist.repository.StyleListRepository

class BeerDetailsViewModel(
    beerId: Int,
    private val beerRepository: BeerRepository,
    private val brewerRepository: BrewerRepository,
    private val styleRepository: StyleRepository,
    private val styleListRepository: StyleListRepository
) : ViewModel() {

    private val _beerState = MutableLiveData<State<Beer>>()
    val beerState: LiveData<State<Beer>> = _beerState

    private val _brewerState = MutableLiveData<State<Brewer>>()
    val brewerState: LiveData<State<Brewer>> = _brewerState

    private val _styleState = MutableLiveData<State<Style>>()
    val styleState: LiveData<State<Style>> = _styleState

    init {
        viewModelScope.launch {
            beerRepository.getStream(beerId, Beer.DetailsDataValidator())
                .collect {
                    _beerState.postValue(it)
                    if (it is State.Success) {
                        getBrewer(it.value)
                        getStyle(it.value)
                    }
                }
        }
    }

    private fun getBrewer(beer: Beer) {
        if (beer.brewerId == null) return

        viewModelScope.launch {
            brewerRepository.getStream(beer.brewerId, Brewer.BasicDataValidator())
                .collect { _brewerState.postValue(it) }
        }
    }

    private fun getStyle(beer: Beer) {
        when {
            beer.styleId != null -> getStyle(beer.styleId)
            beer.styleName != null -> getStyle(beer.styleName)
        }
    }

    private fun getStyle(styleId: Int) {
        viewModelScope.launch {
            styleRepository.getStream(styleId, Accept())
                .collect { _styleState.postValue(it) }
        }
    }

    private fun getStyle(styleName: String) {
        viewModelScope.launch {
            styleListRepository.getStream(Accept())
                .firstOrNull { it is State.Success }
                ?.let { if (it is State.Success) it.value else null }
                ?.firstOrNull { style -> style.name == styleName }
                ?.let { getStyle(it.id) }
        }
    }
}
