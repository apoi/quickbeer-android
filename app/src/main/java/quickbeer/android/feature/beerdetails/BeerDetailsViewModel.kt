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
import kotlinx.coroutines.launch
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository

class BeerDetailsViewModel(
    beerId: Int,
    private val repository: BeerRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<State<Beer>>()
    val viewState: LiveData<State<Beer>> = _viewState

    init {
        viewModelScope.launch {
            repository.getStream(beerId, Accept())
                .collect { _viewState.postValue(it) }
        }
    }
}
