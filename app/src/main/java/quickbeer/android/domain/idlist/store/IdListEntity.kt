package quickbeer.android.domain.idlist.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import quickbeer.android.data.room.converter.IntListConverter
import quickbeer.android.data.store.Merger

@Entity(tableName = "lists")
@TypeConverters(IntListConverter::class)
class IdListEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "values") val values: List<Int>
) {

    companion object {
        val merger: Merger<IdListEntity> = { _, new -> new }
    }
}
