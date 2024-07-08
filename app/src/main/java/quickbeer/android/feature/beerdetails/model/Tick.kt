package quickbeer.android.feature.beerdetails.model

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.state.State
import quickbeer.android.domain.beer.Beer

data class Tick(
    val tick: Int?,
    val tickDate: ZonedDateTime?,
    val isSaving: Boolean
){

    companion object {
        fun create(beer: Beer?): Tick {
            return Tick(
                tick = beer?.tickValue?.takeIf { beer.isTicked() },
                tickDate = beer?.tickDate?.takeIf { beer.isTicked() },
                isSaving = false
            )
        }
    }
}
