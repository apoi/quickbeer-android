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

        fun create(beer: State<Beer>, ratings: State<List<Rating>>): OwnRating {
            return OwnRating(
                beer.valueOrNull()?.tickValue,
                beer.valueOrNull()?.tickDate,
                findRating(beer.valueOrNull(), ratings.valueOrNull())
            )
        }

        private fun findRating(beer: Beer?, ratings: List<Rating>?): Rating? {
            return ratings?.firstOrNull { it.beerId == beer?.id }
        }
    }
}
