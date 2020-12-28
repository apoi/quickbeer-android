package quickbeer.android.domain.brewer.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.brewer.Brewer

@Entity(tableName = "brewers")
@TypeConverters(ZonedDateTimeConverter::class)
data class BrewerEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "address") val address: String?,
    @ColumnInfo(name = "city") val city: String?,
    @ColumnInfo(name = "state_id") val stateId: Int?,
    @ColumnInfo(name = "country_id") val countryId: Int?,
    @ColumnInfo(name = "zip_code") val zipCode: String?,
    @ColumnInfo(name = "type_id") val typeId: Int?,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "website") val website: String?,
    @ColumnInfo(name = "facebook") val facebook: String?,
    @ColumnInfo(name = "twitter") val twitter: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "barrels") val barrels: Int?,
    @ColumnInfo(name = "founded") val founded: ZonedDateTime?,
    @ColumnInfo(name = "entered_on") val enteredOn: ZonedDateTime?,
    @ColumnInfo(name = "entered_by") val enteredBy: Int?,
    @ColumnInfo(name = "logo") val logo: String?,
    @ColumnInfo(name = "view_count") val viewCount: String?,
    @ColumnInfo(name = "score") val score: Int?,
    @ColumnInfo(name = "out_of_business") val outOfBusiness: Boolean?,
    @ColumnInfo(name = "retired") val retired: Boolean?,
    @ColumnInfo(name = "area_code") val areaCode: String?,
    @ColumnInfo(name = "hours") val hours: String?,
    @ColumnInfo(name = "head_brewer") val headBrewer: String?,
    @ColumnInfo(name = "metro_id") val metroId: String?,
    @ColumnInfo(name = "msa") val msa: String?,
    @ColumnInfo(name = "region_id") val regionId: String?
) {

    companion object {
        val merger: Merger<BrewerEntity> = { old, new ->
            val merged = Brewer.merger(BrewerEntityMapper.mapTo(old), BrewerEntityMapper.mapTo(new))
            BrewerEntityMapper.mapFrom(merged)
        }
    }
}
