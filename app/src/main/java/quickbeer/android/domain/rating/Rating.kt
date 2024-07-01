package quickbeer.android.domain.rating

import android.os.Parcelable
import kotlin.random.Random
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.user.User

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
    val rateCount: Int?,
    val isDraft: Boolean
) : Parcelable {

    fun avatarUri(): String {
        return Constants.USER_AVATAR_PATH.format(userName)
    }

    companion object {

        fun createDraft(beerId: Int, user: User): Rating {
            val currentTime = ZonedDateTime.now()
            return Rating(
                id = Random(currentTime.toEpochSecond()).nextInt(),
                beerId = beerId,
                appearance = 0,
                aroma = 0,
                flavor = 0,
                mouthfeel = 0,
                overall = 0,
                totalScore = 0f,
                comments = "",
                timeEntered = currentTime,
                timeUpdated = currentTime,
                userId = user.id,
                userName = user.username,
                city = null,
                stateId = null,
                state = null,
                countryId = null,
                country = null,
                rateCount = null,
                isDraft = true
            )
        }

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
                rateCount = new.rateCount ?: old.rateCount,
                isDraft = new.isDraft ?: old.isDraft
            )
        }
    }
}
