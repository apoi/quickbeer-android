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
    @ColumnInfo(name = "brewer_id") val brewerId: Int?,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "plain_name") val plainName: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "map_id") val mapId: String?,
    @ColumnInfo(name = "tour_id") val tourId: Int?,
    @ColumnInfo(name = "region_id") val regionId: Int?,
    @ColumnInfo(name = "msa") val msa: String?,
    @ColumnInfo(name = "latitude") val latitude: Double?,
    @ColumnInfo(name = "longitude") val longitude: Double?,
    @ColumnInfo(name = "geo_override") val geoOverride: Boolean?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "state_id") val stateId: Int?,
    @ColumnInfo(name = "country_id") val countryId: Int?,
    @ColumnInfo(name = "postal_code") val postalCode: String?,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "phone_country_code") val phoneCountryCode: String?,
    @ColumnInfo(name = "phone_area_code") val phoneAreaCode: String?,
    @ColumnInfo(name = "website") val website: String?,
    @ColumnInfo(name = "facebook") val facebook: String?,
    @ColumnInfo(name = "twitter") val twitter: String?,
    @ColumnInfo(name = "food_menu") val foodMenu: String?,
    @ColumnInfo(name = "beer_menu") val beerMenu: String?,
    @ColumnInfo(name = "rating") val rating: String?,
    @ColumnInfo(name = "avg_rating") val avgRating: Float?,
    @ColumnInfo(name = "bay_mean") val bayMean: Float?,
    @ColumnInfo(name = "percentile") val percentile: Int?,
    @ColumnInfo(name = "rate_count") val rateCount: Int?,
    @ColumnInfo(name = "taps") val taps: String?,
    @ColumnInfo(name = "bottles") val bottles: String?,
    @ColumnInfo(name = "seasonals") val seasonals: Boolean?,
    @ColumnInfo(name = "real_ales") val realAles: Boolean?,
    @ColumnInfo(name = "rare_beers") val rareBeers: Boolean?,
    @ColumnInfo(name = "macros") val macros: Boolean?,
    @ColumnInfo(name = "glassware") val glassware: Boolean?,
    @ColumnInfo(name = "singles") val singles: Boolean?,
    @ColumnInfo(name = "togo") val togo: Boolean?,
    @ColumnInfo(name = "wifi") val wifi: Boolean?,
    @ColumnInfo(name = "children") val children: Boolean?,
    @ColumnInfo(name = "reservations") val reservations: Boolean?,
    @ColumnInfo(name = "patio") val patio: Boolean?,
    @ColumnInfo(name = "tv") val tv: Boolean?,
    @ColumnInfo(name = "games") val games: Boolean?,
    @ColumnInfo(name = "scene") val scene: String?,
    @ColumnInfo(name = "staff") val staff: String?,
    @ColumnInfo(name = "parking") val parking: String?,
    @ColumnInfo(name = "music") val music: String?,
    @ColumnInfo(name = "events") val events: String?,
    @ColumnInfo(name = "noise") val noise: String?,
    @ColumnInfo(name = "seating") val seating: String?,
    @ColumnInfo(name = "smoking") val smoking: Boolean?,
    @ColumnInfo(name = "cigars") val cigars: Boolean?,
    @ColumnInfo(name = "food") val food: String?,
    @ColumnInfo(name = "hours") val hours: String?,
    @ColumnInfo(name = "comments") val comments: String?,
    @ColumnInfo(name = "currency") val currency: String?,
    @ColumnInfo(name = "mail_order") val mailOrder: Boolean?,
    @ColumnInfo(name = "mail_order_intl") val mailOrderInternational: Boolean?,
    @ColumnInfo(name = "retired") val retired: Boolean?,
    @ColumnInfo(name = "user_id") val userId: Int?,
    @ColumnInfo(name = "edited_by") val editedBy: Int?,
    @ColumnInfo(name = "time_added") val timeAdded: ZonedDateTime?,
    @ColumnInfo(name = "time_edited") val timeEdited: ZonedDateTime?,
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
