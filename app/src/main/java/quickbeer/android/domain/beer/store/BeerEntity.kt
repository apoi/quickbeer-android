package quickbeer.android.domain.beer.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.beer.Beer

@Entity(tableName = "beers", indices = [Index(value = ["normalized_name", "accessed"])])
@TypeConverters(ZonedDateTimeConverter::class)
data class BeerEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "brewer_id") val brewerId: Int?,
    @ColumnInfo(name = "brewer_name") val brewerName: String?,
    @ColumnInfo(name = "contract_brewer_id") val contractBrewerId: Int?,
    @ColumnInfo(name = "contract_brewer_name") val contractBrewerName: String?,
    @ColumnInfo(name = "average_rating") val averageRating: Float?,
    @ColumnInfo(name = "rating_overall") val overallRating: Float?,
    @ColumnInfo(name = "rating_style") val styleRating: Float?,
    @ColumnInfo(name = "rate_count") val rateCount: Int?,
    @ColumnInfo(name = "brewer_country_id") val countryId: Int?,
    @ColumnInfo(name = "beer_style_id") val styleId: Int?,
    @ColumnInfo(name = "beer_style_name") val styleName: String?,
    @ColumnInfo(name = "alcohol") val alcohol: Float?,
    @ColumnInfo(name = "ibu") val ibu: Float?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "is_alias") val isAlias: Boolean?,
    @ColumnInfo(name = "is_retired") val isRetired: Boolean?,
    @ColumnInfo(name = "is_verified") val isVerified: Boolean?,
    @ColumnInfo(name = "is_unrateable") val unrateable: Boolean?,
    @ColumnInfo(name = "liked") val tickValue: Int?,
    @ColumnInfo(name = "time_entered") val tickDate: ZonedDateTime?,
    @ColumnInfo(name = "normalized_name") val normalizedName: String?,
    @ColumnInfo(name = "updated") val updated: ZonedDateTime?,
    @ColumnInfo(name = "accessed") val accessed: ZonedDateTime?
) {

    companion object {
        val merger: Merger<BeerEntity> = { old, new ->
            val merged = Beer.merger(BeerEntityMapper.mapTo(old), BeerEntityMapper.mapTo(new))
            BeerEntityMapper.mapFrom(merged)
        }
    }
}
