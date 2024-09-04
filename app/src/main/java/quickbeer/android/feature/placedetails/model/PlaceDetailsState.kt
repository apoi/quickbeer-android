package quickbeer.android.feature.placedetails.model

import quickbeer.android.data.state.State
import quickbeer.android.domain.address.Address
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.place.Place

data class PlaceDetailsState(
    val place: Place,
    val brewer: Brewer?,
    val address: Address?
) {

    companion object {
        fun create(
            place: State<Place>,
            brewer: State<Brewer>,
            address: State<Address>
        ): PlaceDetailsState? {
            val placeValue = place.valueOrNull() ?: return null
            return PlaceDetailsState(
                place = placeValue,
                brewer = brewer.valueOrNull(),
                address = address.valueOrNull()
            )
        }
    }
}
