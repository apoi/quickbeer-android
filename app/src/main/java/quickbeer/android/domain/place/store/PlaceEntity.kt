package quickbeer.android.domain.place.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.place.Place

@Entity(
    tableName = "places",
    indices = [
        Index(value = ["normalized_name"]),
        Index(value = ["accessed"])
    ]
)
@TypeConverters(ZonedDateTimeConverter::class)
data class PlaceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "brewer_id") val brewerId: Long?,
    @ColumnInfo(name = "user_id") val userId: Long?,
    @ColumnInfo(name = "is_retired") val isRetired: Boolean?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "postal_code") val postalCode: String?,
    @ColumnInfo(name = "country_id") val countryId: Int?,
    @ColumnInfo(name = "state_id") val stateId: Int?,
    @ColumnInfo(name = "hours") val hours: String?,
    @ColumnInfo(name = "taps") val taps: String?,
    @ColumnInfo(name = "bottles") val bottles: String?,
    @ColumnInfo(name = "website") val website: String?,
    @ColumnInfo(name = "facebook") val facebook: String?,
    @ColumnInfo(name = "twitter") val twitter: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "latitude") val latitude: Float?,
    @ColumnInfo(name = "longitude") val longitude: Float?,
    @ColumnInfo(name = "real_rating") val realRating: Float?,
    @ColumnInfo(name = "weighted_rating") val weightedRating: Float?,
    @ColumnInfo(name = "overall_percentile") val overallPercentile: Float?,
    @ColumnInfo(name = "rate_count") val rateCount: Int?,
    @ColumnInfo(name = "normalized_name") val normalizedName: String?,
    @ColumnInfo(name = "updated") val updated: ZonedDateTime?,
    @ColumnInfo(name = "accessed") val accessed: ZonedDateTime?
) {

    companion object {
        val merger: Merger<PlaceEntity> = { old, new ->
            val merged = Place.merger(PlaceEntityMapper.mapTo(old), PlaceEntityMapper.mapTo(new))
            PlaceEntityMapper.mapFrom(merged)
        }
    }
}
