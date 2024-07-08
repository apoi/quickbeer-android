package quickbeer.android.feature.beerdetails.model

import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.style.Style
import quickbeer.android.domain.user.User

data class BeerDetailsState(
    val beer: Beer,
    val brewer: Brewer?,
    val style: Style?,
    val address: Address?,
    val user: User?,
    val rating: RatingState<Rating>?,
    val tick: RatingState<Tick>?
) {

    companion object {

        fun create(
            beer: State<Beer>,
            brewer: State<Brewer>,
            style: State<Style>,
            address: State<Address>,
            user: State<User>,
            rating: RatingState<Rating>,
            tick: RatingState<Tick>
        ): BeerDetailsState? {
            val beerValue = beer.valueOrNull() ?: return null
            return BeerDetailsState(
                beer = beerValue,
                brewer = brewer.valueOrNull(),
                style = style.valueOrNull(),
                address = address.valueOrNull(),
                user = user.valueOrNull(),
                rating = rating,
                tick = tick
            )
        }
    }
}
