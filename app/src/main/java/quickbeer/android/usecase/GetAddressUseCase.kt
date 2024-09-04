package quickbeer.android.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.state.State
import quickbeer.android.domain.address.Address
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.place.Place

// TODO some combination opportunities here surely
class GetAddressUseCase @Inject constructor() {

    fun getBrewerAddress(
        brewerFlow: Flow<State<Brewer>>,
        countryFlow: Flow<State<Country>>
    ): Flow<State<Address>> {
        return brewerFlow
            .combine(countryFlow, ::mergeBrewerAddress)
            .onStart { emit(State.Initial) }
    }

    fun getPlaceAddress(
        placeFlow: Flow<State<Place>>,
        countryFlow: Flow<State<Country>>
    ): Flow<State<Address>> {
        return placeFlow
            .combine(countryFlow, ::mergePlaceAddress)
            .onStart { emit(State.Initial) }
    }

    private fun mergeBrewerAddress(
        brewerFlow: State<Brewer>,
        countryFlow: State<Country>
    ): State<Address> {
        return if (brewerFlow is State.Success && countryFlow is State.Success) {
            State.Success(Address.from(brewerFlow.value, countryFlow.value))
        } else {
            State.Loading()
        }
    }

    private fun mergePlaceAddress(
        placeFlow: State<Place>,
        countryFlow: State<Country>
    ): State<Address> {
        return if (placeFlow is State.Success && countryFlow is State.Success) {
            State.Success(Address.from(placeFlow.value, countryFlow.value))
        } else {
            State.Loading()
        }
    }
}
