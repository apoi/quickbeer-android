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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.repository.Accept
import quickbeer.android.data.repository.NoFetch
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.repository.BeerRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.repository.BrewerRepository
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.country.repository.CountryRepository
import quickbeer.android.domain.ratinglist.repository.UserRatingRepository
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.style.repository.StyleRepository
import quickbeer.android.domain.stylelist.repository.StyleListRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.repository.CurrentUserRepository
import quickbeer.android.feature.beerdetails.model.Address
import quickbeer.android.feature.beerdetails.model.OwnRating
import quickbeer.android.util.ktx.navId

@HiltViewModel
class BeerDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currentUserRepository: CurrentUserRepository,
    private val beerRepository: BeerRepository,
    private val brewerRepository: BrewerRepository,
    private val styleRepository: StyleRepository,
    private val styleListRepository: StyleListRepository,
    private val countryRepository: CountryRepository,
    private val userRatingRepository: UserRatingRepository
) : ViewModel() {

    private val beerId = savedStateHandle.navId()

    private val _ratingState = MutableStateFlow<State<Pair<User?, OwnRating>>>(State.Initial)
    val ratingState: Flow<State<Pair<User?, OwnRating>>> = _ratingState

    private val _beerState = MutableStateFlow<State<Beer>>(State.Initial)
    val beerState: Flow<State<Beer>> = _beerState

    private val _brewerState = MutableStateFlow<State<Brewer>>(State.Initial)
    val brewerState: Flow<State<Brewer>> = _brewerState

    private val _styleState = MutableStateFlow<State<Style>>(State.Initial)
    val styleState: Flow<State<Style>> = _styleState

    private val _addressState = MutableStateFlow<State<Address>>(State.Initial)
    val addressState: Flow<State<Address>> = _addressState

    init {
        updateAccessedBeer(beerId)

        viewModelScope.launch(Dispatchers.IO) {
            beerRepository.getStream(beerId, Beer.DetailsDataValidator())
                .collectLatest {
                    _beerState.emit(it)

                    if (it is State.Success) {
                        getBrewer(it.value)
                        getStyle(it.value)
                        getAddress(it.value)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val userFlow = currentUserRepository.getStream(NoFetch())
            val beerFlow = beerRepository.getStream(beerId, Beer.DetailsDataValidator())
            val ratingFlow = userRatingRepository.getStream(NoFetch())

            beerFlow.combine(ratingFlow, OwnRating.Companion::create)
                .combine(userFlow) { ownRating, user -> Pair(user.valueOrNull(), ownRating) }
                .collectLatest {
                    _ratingState.emit(State.from(it))
                }
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

    private fun updateAccessedBrewer(brewerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            brewerRepository.getStream(brewerId, Accept())
                .filterIsInstance<State.Success<Brewer>>()
                .map { it.value }
                .take(1)
                .collectLatest { brewer ->
                    val accessed = brewer.copy(accessed = ZonedDateTime.now())
                    brewerRepository.persist(brewer.id, accessed)
                }
        }
    }

    private fun getBrewer(beer: Beer) {
        if (beer.brewerId == null) return

        updateAccessedBrewer(beer.brewerId)

        viewModelScope.launch(Dispatchers.IO) {
            brewerRepository.getStream(beer.brewerId, Brewer.BasicDataValidator())
                .collectLatest(_brewerState::emit)
        }
    }

    private fun getStyle(beer: Beer) {
        when {
            beer.styleId != null -> getStyle(beer.styleId)
            beer.styleName != null -> getStyle(beer.styleName)
        }
    }

    private fun getStyle(styleId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            styleRepository.getStream(styleId, Accept())
                .collectLatest(_styleState::emit)
        }
    }

    private fun getStyle(styleName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            styleListRepository.getStream(Accept())
                .firstOrNull { it is State.Success }
                ?.let { if (it is State.Success) it.value else null }
                ?.firstOrNull { style -> style.name == styleName }
                ?.let { getStyle(it.id) }
        }
    }

    private fun getAddress(beer: Beer) {
        if (beer.brewerId == null || beer.countryId == null) return

        val brewer = brewerRepository.getStream(beer.brewerId, Brewer.DetailsDataValidator())
        val country = countryRepository.getStream(beer.countryId, Accept())

        viewModelScope.launch(Dispatchers.IO) {
            brewer.combineTransform(country) { b, c ->
                emit(mergeAddress(b, c))
            }.collectLatest { _addressState.emit(it) }
        }
    }

    private fun mergeAddress(brewer: State<Brewer>, country: State<Country>): State<Address> {
        return if (brewer is State.Success && country is State.Success) {
            State.Success(Address.from(brewer.value, country.value))
        } else {
            State.Loading()
        }
    }

    /* TODO logic to review fragment
    fun tickBeer(tick: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val beer = beerRepository.store.get(beerId) ?: error("No beer found!")
            val userId = loginManager.userId.first() ?: error("Not logged in!")
            val fetchKey = BeerTickFetcher.TickKey(beerId, userId, tick)
            val result = beerTickFetcher.fetch(fetchKey)

            if (result is ApiResult.Success) {
                val update = beer.copy(tickValue = tick, tickDate = ZonedDateTime.now())
                beerRepository.persist(beerId, update)
            }

            val message = when {
                result !is ApiResult.Success -> resourceProvider.getString(R.string.tick_failure)
                tick > 0 -> resourceProvider.getString(R.string.tick_success).format(beer.name)
                else -> resourceProvider.getString(R.string.tick_removed)
            }

            withContext(Dispatchers.Main) {
                toastProvider.showToast(message)
            }
        }
    }
     */
}
