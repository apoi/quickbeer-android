package quickbeer.android.data.room.converter

import androidx.room.TypeConverter
import org.threeten.bp.ZonedDateTime

class ZonedDateTimeConverter {

    @TypeConverter
    fun fromString(value: String?): ZonedDateTime? {
        return value?.let(ZonedDateTime::parse)
    }

    @TypeConverter
    fun toString(date: ZonedDateTime?): String? {
        return date?.toString()
    }
}
