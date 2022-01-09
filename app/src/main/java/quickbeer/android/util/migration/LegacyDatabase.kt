package quickbeer.android.util.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.OPEN_READONLY
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.brewer.Brewer
import timber.log.Timber

class LegacyDatabase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Suppress("TooGenericExceptionCaught")
    fun getBeers(): List<Beer> {
        if (!databaseExists(context)) {
            // Nothing to migrate
            return emptyList()
        }

        return try {
            readBeers()
        } catch (e: Exception) {
            Timber.e(e, "Failure with v3 migration")
            emptyList()
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun getBrewers(): List<Brewer> {
        if (!databaseExists(context)) {
            // Nothing to migrate
            return emptyList()
        }

        return try {
            readBrewers()
        } catch (e: Exception) {
            Timber.e(e, "Failure with v3 migration")
            emptyList()
        }
    }

    private fun readBeers(): List<Beer> {
        val db = getDatabase(context)
        val cursor = db.rawQuery("SELECT beer_id, updated, accessed FROM beerMetadata", null)
        val values = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { LegacyBeerReader.getBeerFromCursor(it) }
            .toList()

        cursor.close()
        db.close()

        return values
    }

    private fun readBrewers(): List<Brewer> {
        val db = getDatabase(context)
        val cursor = db.rawQuery("SELECT brewer_d, updated, accessed FROM brewerMetadata", null)
        val values = generateSequence { if (cursor.moveToNext()) cursor else null }
            .map { LegacyBrewerReader.getBrewerFromCursor(it) }
            .toList()

        cursor.close()
        db.close()

        return values
    }

    private fun getDatabasePath(context: Context): String {
        return "${context.applicationInfo.dataDir}/databases/$LEGACY_DATABASE_NAME"
    }

    private fun databaseExists(context: Context): Boolean {
        val file = File(getDatabasePath(context))
        return file.exists()
    }

    private fun getDatabase(context: Context): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(getDatabasePath(context), null, OPEN_READONLY)
    }

    companion object {
        private const val LEGACY_DATABASE_NAME = "rateBeerDatabase.db"
    }
}
