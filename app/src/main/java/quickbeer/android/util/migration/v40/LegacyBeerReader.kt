package quickbeer.android.util.migration.v40

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.beer.Beer

object LegacyBeerReader {

    fun readBeers(db: SQLiteDatabase): List<Beer> {
        val cursor = db.rawQuery("SELECT beer_id, updated, accessed FROM beerMetadata", null)
        val values = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { readBeerFromCursor(it) }
            .toList()

        cursor.close()

        return values
    }

    @SuppressLint("Range")
    private fun readBeerFromCursor(cursor: Cursor): Beer {
        val id = cursor.getInt(cursor.getColumnIndex("beer_id"))
        val updated = cursor.getInt(cursor.getColumnIndex("updated"))
        val accessed = cursor.getInt(cursor.getColumnIndex("accessed"))
        return createBeer(id, updated.toLong(), accessed.toLong())
    }

    private fun createBeer(id: Int, updated: Long, accessed: Long): Beer {
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

        return Beer(
            id = id,
            name = null,
            brewerId = null,
            brewerName = null,
            contractBrewerId = null,
            contractBrewerName = null,
            averageRating = null,
            overallRating = null,
            styleRating = null,
            rateCount = null,
            countryId = null,
            styleId = null,
            styleName = null,
            alcohol = null,
            ibu = null,
            description = null,
            isAlias = null,
            isRetired = null,
            isVerified = null,
            unrateable = null,
            tickValue = null,
            tickDate = null,
            normalizedName = null,
            updated = dateUpdated,
            accessed = dateAccessed
        )
    }
}
