package quickbeer.android.feature.beerdetails.model

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.rating.Rating

data class OwnRating(
    val tick: Int?,
    val tickDate: ZonedDateTime?,
    val rating: Rating?
) {

    companion object {

        fun create(beerState: State<Beer>, ratingsState: State<List<Rating>>): OwnRating {
            val beer = beerState.valueOrNull()
            val ratings = ratingsState.valueOrNull()

            return OwnRating(
                beer?.tickValue?.takeIf { beer.isTicked() },
                beer?.tickDate?.takeIf { beer.isTicked() },
                findRating(beer, ratings)
            )
        }

        private fun findRating(beer: Beer?, ratings: List<Rating>?): Rating? {
            return ratings?.firstOrNull { it.beerId == beer?.id }
        }
    }
}
