package quickbeer.android.domain.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import quickbeer.android.data.store.Merger

@Keep
@Parcelize
data class User(
    val id: Int,
    val username: String?,
    val password: String?,
    val loggedIn: Boolean?,
    val countryId: Int?,
    val rateCount: Int?,
    val tickCount: Int?,
    val placeCount: Int?
) : Parcelable {

    companion object {

        fun createCurrentUser(id: Int, username: String, password: String): User {
            return User(
                id = id,
                username = username,
                password = password,
                loggedIn = true,
                countryId = null,
                rateCount = null,
                tickCount = null,
                placeCount = null
            )
        }

        val merger: Merger<User> = { old, new ->
            User(
                id = new.id,
                username = new.username ?: old.username,
                password = new.password ?: old.password,
                loggedIn = new.loggedIn ?: old.loggedIn,
                countryId = new.countryId ?: old.countryId,
                rateCount = new.rateCount ?: old.rateCount,
                tickCount = new.tickCount ?: old.tickCount,
                placeCount = new.placeCount ?: old.placeCount
            )
        }
    }
}
