package quickbeer.android.util.migration.v40

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository
import quickbeer.android.domain.brewer.store.BrewerStore
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.util.migration.ApplicationMigration
import timber.log.Timber

class MigrationV40 @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val beerStore: BeerStore,
    private val brewerStore: BrewerStore,
    private val intPreferenceStore: IntPreferenceStore,
    private val stringPreferenceStore: StringPreferenceStore,
    private val tickedBeersRepository: TickedBeersRepository
) : ApplicationMigration.Migration {

    override suspend fun migrate() {
        // Migration is run only once
        if (intPreferenceStore.get(MIGRATION_V40) != null) return

        intPreferenceStore.put(MIGRATION_V40, 1)

        // Nothing to migrate if database doesn't exist
        val database = getDatabase(context) ?: return

        migrateBeers(database)
        migrateBrewers(database)
        migrateUser(database)

        database.close()
    }

    private suspend fun migrateBeers(database: SQLiteDatabase) {
        try {
            LegacyBeerReader.readBeers(database)
                .forEach { beerStore.put(it.id, it) }
        } catch (e: Exception) {
            Timber.e(e, "Failure migrating v3 beers")
        }
    }

    private suspend fun migrateBrewers(database: SQLiteDatabase) {
        try {
            LegacyBrewerReader.readBrewers(database)
                .forEach { brewerStore.put(it.id, it) }
        } catch (e: Exception) {
            Timber.e(e, "Failure migrating v3 brewers")
        }
    }

    private suspend fun migrateUser(database: SQLiteDatabase) {
        try {
            LegacyUserReader.readUser(database, moshi)
                ?.also { user ->
                    intPreferenceStore.put(USERID, user.id)
                    stringPreferenceStore.put(USERNAME, user.username)
                    stringPreferenceStore.put(PASSWORD, user.password)
                    tickedBeersRepository.fetch(user.id.toString())
                }
        } catch (e: Exception) {
            Timber.e(e, "Failure migrating v3 user")
        }
    }

    private fun getDatabase(context: Context): SQLiteDatabase? {
        val file = File(getDatabasePath(context))
        if (!file.exists()) return null

        return try {
            SQLiteDatabase.openDatabase(
                getDatabasePath(context),
                null,
                SQLiteDatabase.OPEN_READONLY
            )
        } catch (e: Exception) {
            Timber.e(e, "Failure opening legacy database for v3 migration")
            return null
        }
    }

    private fun getDatabasePath(context: Context): String {
        return "${context.applicationInfo.dataDir}/databases/rateBeerDatabase.db"
    }

    companion object {
        private const val MIGRATION_V40 = "MIGRATION_3_4"

        // Legacy preferences keys
        private const val USERID = "PREF_USERID"
        private const val USERNAME = "PREF_USERNAME"
        private const val PASSWORD = "PREF_PASSWORD"
    }
}
