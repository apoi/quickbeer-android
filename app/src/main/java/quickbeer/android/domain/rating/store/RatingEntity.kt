package quickbeer.android.domain.rating.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.rating.Rating

@Entity(tableName = "reviews")
@TypeConverters(ZonedDateTimeConverter::class)
data class RatingEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "beer_id") val beerId: Int?,
    @ColumnInfo(name = "appearance") val appearance: Int?,
    @ColumnInfo(name = "aroma") val aroma: Int?,
    @ColumnInfo(name = "flavor") val flavor: Int?,
    @ColumnInfo(name = "mouthfeel") val mouthfeel: Int?,
    @ColumnInfo(name = "overall") val overall: Int?,
    @ColumnInfo(name = "total_score") val totalScore: Float?,
    @ColumnInfo(name = "comments") val comments: String?,
    @ColumnInfo(name = "time_entered") val timeEntered: ZonedDateTime?,
    @ColumnInfo(name = "time_updated") val timeUpdated: ZonedDateTime?,
    @ColumnInfo(name = "user_id") val userId: Int?,
    @ColumnInfo(name = "user_name") val userName: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "state_id") val stateId: Int?,
    @ColumnInfo(name = "state") val state: String?,
    @ColumnInfo(name = "country_id") val countryId: Int?,
    @ColumnInfo(name = "country") val country: String?,
    @ColumnInfo(name = "rate_count") val rateCount: Int?
) {

    companion object {
        val merger: Merger<RatingEntity> = { old, new ->
            val merged = Rating.merger(RatingEntityMapper.mapTo(old), RatingEntityMapper.mapTo(new))
            RatingEntityMapper.mapFrom(merged)
        }
    }
}
