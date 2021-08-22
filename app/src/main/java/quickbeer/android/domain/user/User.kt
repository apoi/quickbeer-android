package quickbeer.android.domain.user

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.store.Merger

@Parcelize
data class User(
    val id: Int?,
    val username: String? = null,
    val rateCount: Int? = null,
    val tickCount: Int? = null,
    val placeCount: Int? = null
) : Parcelable {

    companion object {
        val merger: Merger<User> = { old, new ->
            User(
                id = new.id,
                username = new.username ?: old.username,
                rateCount = new.rateCount ?: old.rateCount,
                tickCount = new.tickCount ?: old.tickCount,
                placeCount = new.placeCount ?: old.placeCount,
            )
        }
    }

    open class RateCountValidator : Validator<User> {
        override suspend fun validate(user: User?): Boolean {
            return user?.rateCount != null
        }
    }
}
