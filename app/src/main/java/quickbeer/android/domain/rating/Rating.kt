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
    val rateCount: Int?
) : Parcelable {

    fun avatarUri(): String {
        return Constants.USER_AVATAR_PATH.format(userName)
    }

    fun isDraft(): Boolean {
        return id < 0
    }

    fun isValid(): Boolean {
        val scores = listOf(appearance, aroma, flavor, mouthfeel, overall)
        return scores.all { it != null && it > 0 } && comments != null &&
            comments.length >= Constants.RATING_MIN_LENGTH
    }

    fun scoringDiffers(other: Rating): Boolean {
        return appearance != other.appearance ||
            aroma != other.aroma ||
            flavor != other.flavor ||
            mouthfeel != other.mouthfeel ||
            overall != other.overall ||
            comments != other.comments
    }

    companion object {

        fun createDraft(beerId: Int, user: User): Rating {
            val currentTime = ZonedDateTime.now()
            return Rating(
                // Random negative number will work as a local draft ID
                id = Random.nextInt(Int.MAX_VALUE) * -1,
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
                rateCount = null
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
                rateCount = new.rateCount ?: old.rateCount
            )
        }
    }
}
