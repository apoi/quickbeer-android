package quickbeer.android.util.migration.v3

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.brewer.Brewer

object LegacyBrewerReader {

    fun readBrewers(db: SQLiteDatabase): List<Brewer> {
        val cursor = db.rawQuery("SELECT brewer_d, updated, accessed FROM brewerMetadata", null)
        val values = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { readBrewerFromCursor(it) }
            .toList()

        cursor.close()

        return values
    }

    @SuppressLint("Range")
    private fun readBrewerFromCursor(cursor: Cursor): Brewer {
        val id = cursor.getInt(cursor.getColumnIndex("brewer_d"))
        val updated = cursor.getInt(cursor.getColumnIndex("updated"))
        val accessed = cursor.getInt(cursor.getColumnIndex("accessed"))
        return createBrewer(id, updated.toLong(), accessed.toLong())
    }

    private fun createBrewer(id: Int, updated: Long, accessed: Long): Brewer {
        val dateUpdated = if (updated > 0) {
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(updated), ZoneOffset.UTC)
        } else {
            null
        }

        val dateAccessed = if (accessed > 0) {
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(accessed), ZoneOffset.UTC)
        } else {
            null
        }

        return Brewer(
            id = id,
            name = null,
            description = null,
            address = null,
            city = null,
            stateId = null,
            countryId = null,
            zipCode = null,
            typeId = null,
            type = null,
            website = null,
            facebook = null,
            twitter = null,
            email = null,
            phone = null,
            barrels = null,
            founded = null,
            enteredOn = null,
            enteredBy = null,
            logo = null,
            viewCount = null,
            score = null,
            outOfBusiness = null,
            retired = null,
            areaCode = null,
            hours = null,
            headBrewer = null,
            metroId = null,
            msa = null,
            regionId = null,
            normalizedName = null,
            updated = dateUpdated,
            accessed = dateAccessed
        )
    }
}
