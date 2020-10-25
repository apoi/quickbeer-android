package quickbeer.android.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.Locale
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class ZonedDateTimeAdapter {

    @ToJson
    fun serialize(date: ZonedDateTime): String {
        return date.format(ISO_FORMAT)
    }

    @FromJson
    fun deserialize(json: String): ZonedDateTime {
        return when {
            json.contains("T") -> {
                ZonedDateTime.parse(json, ISO_FORMAT)
            }
            json.contains(" ") -> {
                ZonedDateTime.parse(json, US_DATETIME_FORMAT)
                    .withZoneSameInstant(ZoneOffset.UTC)
            }
            else -> {
                LocalDate.parse(json, US_DATE_FORMAT)
                    .atStartOfDay(ZoneOffset.UTC)
            }
        }
    }

    companion object {

        private val SERVER_ZONE =
            ZoneId.ofOffset("", ZoneOffset.ofHours(-5))

        private val ISO_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                .withZone(ZoneOffset.UTC)

        private val US_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a")
                .withLocale(Locale.US)
                .withZone(SERVER_ZONE)

        private val US_DATE_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy")
    }
}
