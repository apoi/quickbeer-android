package quickbeer.android.domain.rating

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger

@Parcelize
data class Rating(
    val id: Int,
    val beerId: Int?,
    val appearance: Int?,
    val aroma: Int?,
    val flavor: Int?,
    val mouthfeel: Int?,
    val overall: Int?,
    val totalScore: Float?,
    val comments: String?,
    val timeEntered: ZonedDateTime?,
    val timeUpdated: ZonedDateTime?,
    val userId: Int?,
    val userName: String?,
    val city: String?,
    val stateId: Int?,
    val state: String?,
    val countryId: Int?,
    val country: String?,
    val rateCount: Int?
) : Parcelable {

    companion object {
        val merger: Merger<Rating> = { old, new ->
            Rating(
                id = new.id,
                beerId = new.beerId ?: old.beerId,
                appearance = new.appearance ?: old.appearance,
                aroma = new.aroma ?: old.aroma,
                flavor = new.flavor ?: old.flavor,
                mouthfeel = new.mouthfeel ?: old.mouthfeel,
                overall = new.overall ?: old.overall,
                totalScore = new.totalScore ?: old.totalScore,
                comments = new.comments ?: old.comments,
                timeEntered = new.timeEntered ?: old.timeEntered,
                timeUpdated = new.timeUpdated ?: old.timeUpdated,
                userId = new.userId ?: old.userId,
                userName = new.userName ?: old.userName,
                city = new.city ?: old.city,
                stateId = new.stateId ?: old.stateId,
                state = new.state ?: old.state,
                countryId = new.countryId ?: old.countryId,
                country = new.country ?: old.country,
                rateCount = new.rateCount ?: old.rateCount
            )
        }
    }
}
