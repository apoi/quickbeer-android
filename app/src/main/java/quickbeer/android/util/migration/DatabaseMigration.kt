package quickbeer.android.util.migration

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quickbeer.android.domain.beer.store.BeerStore
import quickbeer.android.domain.brewer.store.BrewerStore

class DatabaseMigration @Inject constructor(
    private val beerStore: BeerStore,
    private val brewerStore: BrewerStore,
    private val legacyDatabase: LegacyDatabase
) {

    private val migrationContext = CoroutineScope(Dispatchers.IO)

    fun migrate() {
        migrationContext.launch {
            legacyDatabase.getBeers()
                .forEach { beerStore.put(it.id, it) }
        }

        migrationContext.launch {
            legacyDatabase.getBrewers()
                .forEach { brewerStore.put(it.id, it) }
        }
    }
}
