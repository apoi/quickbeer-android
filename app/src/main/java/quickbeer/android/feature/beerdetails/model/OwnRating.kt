package quickbeer.android.feature.beerdetails.model

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.rating.Rating

data class OwnRating(
    val rating: Rating?,
    val tick: Int?,
    val tickDate: ZonedDateTime?
)
