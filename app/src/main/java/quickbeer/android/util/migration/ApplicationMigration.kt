package quickbeer.android.util.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.OPEN_READONLY
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quickbeer.android.Constants
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.beerlist.repository.TickedBeersRepository
import quickbeer.android.domain.brewer.store.BrewerStore
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.util.migration.v3.LegacyBeerReader
import quickbeer.android.util.migration.v3.LegacyBrewerReader
import quickbeer.android.util.migration.v3.LegacyUserReader
import timber.log.Timber

class ApplicationMigration @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
    private val beerStore: BeerStore,
    private val brewerStore: BrewerStore,
    private val intPreferenceStore: IntPreferenceStore,
    private val stringPreferenceStore: StringPreferenceStore,
    private val tickedBeersRepository: TickedBeersRepository
) {

    private val migrationContext = CoroutineScope(Dispatchers.IO)

    fun migrate() {
        migrationContext.launch {
            migrateV3toV4()
        }
    }

    private suspend fun migrateV3toV4() {
        // Migration is run only once
        if (intPreferenceStore.get(MIGRATION_3_4) != null) return

        intPreferenceStore.put(MIGRATION_3_4, 1)

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
                    intPreferenceStore.put(Constants.USERID, user.id)
                    stringPreferenceStore.put(Constants.USERNAME, user.username)
                    stringPreferenceStore.put(Constants.PASSWORD, user.password)
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
            SQLiteDatabase.openDatabase(getDatabasePath(context), null, OPEN_READONLY)
        } catch (e: Exception) {
            Timber.e(e, "Failure opening legacy database for v3 migration")
            return null
        }
    }

    private fun getDatabasePath(context: Context): String {
        return "${context.applicationInfo.dataDir}/databases/rateBeerDatabase.db"
    }

    companion object {
        private const val MIGRATION_3_4 = "MIGRATION_3_4"
    }
}
