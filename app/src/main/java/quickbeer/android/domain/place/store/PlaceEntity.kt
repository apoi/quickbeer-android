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

@Entity(tableName = "places", indices = [Index(value = ["normalized_name", "accessed"])])
@TypeConverters(ZonedDateTimeConverter::class)
data class PlaceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String?,
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
