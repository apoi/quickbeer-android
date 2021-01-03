package quickbeer.android.domain.idlist.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.room.converter.IntListConverter
import quickbeer.android.data.room.converter.ZonedDateTimeConverter
import quickbeer.android.data.store.Merger

@Entity(tableName = "lists")
@TypeConverters(value = [IntListConverter::class, ZonedDateTimeConverter::class])
class IdListEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "values") val values: List<Int>,
    @ColumnInfo(name = "updated") val updated: ZonedDateTime
) {

    companion object {
        val merger: Merger<IdListEntity> = { _, new -> new }
    }
}
