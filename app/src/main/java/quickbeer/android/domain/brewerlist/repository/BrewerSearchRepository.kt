package quickbeer.android.domain.brewerlist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.brewerlist.network.BrewerSearchFetcher
import quickbeer.android.domain.brewerlist.store.BrewerSearchStore

class BrewerSearchRepository @Inject constructor(
    override val store: BrewerSearchStore,
    fetcher: BrewerSearchFetcher
) : ItemListRepository<String, Int, Brewer, BrewerJson>(store, fetcher) {

    override fun getLocalStream(key: String): Flow<List<Brewer>> {
        // Usual exact match for the key, starting with empty list if no value is stored
        val exactMatch = super.getLocalStream(key)
            .onStart { emit(getLocal(key).orEmpty()) }
            .distinctUntilChanged()

        // Additional keyword matches from other searches
        val fuzzyMatch = store.search(key)

        return combine(exactMatch, fuzzyMatch) { a, b -> a + b }
            .map { it.distinctBy(Brewer::id) }
    }
}
