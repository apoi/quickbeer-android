package quickbeer.android.domain.style.store

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import quickbeer.android.data.store.Merger
import quickbeer.android.domain.style.Style

@Entity(tableName = "styles")
data class StyleEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "parent") val parent: Int?,
    @ColumnInfo(name = "category") val category: Int?
) {

    companion object {
        val merger: Merger<StyleEntity> = { old, new ->
            val merged = Style.merger(StyleEntityMapper.mapTo(old), StyleEntityMapper.mapTo(new))
            StyleEntityMapper.mapFrom(merged)
        }
    }
}
