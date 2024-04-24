package quickbeer.android.domain.user.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.user.User

@Entity(tableName = "users", indices = [Index(value = ["logged_in"])])
data class UserEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "logged_in") val loggedIn: Boolean?,
    @ColumnInfo(name = "country_id") val countryId: Int?,
    @ColumnInfo(name = "rate_count") val rateCount: Int?,
    @ColumnInfo(name = "tick_count") val tickCount: Int?,
    @ColumnInfo(name = "place_count") val placeCount: Int?
) {

    companion object {
        val merger: Merger<UserEntity> = { old, new ->
            val merged = User.merger(UserEntityMapper.mapTo(old), UserEntityMapper.mapTo(new))
            UserEntityMapper.mapFrom(merged)
        }
    }
}
