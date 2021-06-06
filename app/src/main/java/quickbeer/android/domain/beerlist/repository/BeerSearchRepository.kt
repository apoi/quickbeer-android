package quickbeer.android.domain.beerlist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beerlist.network.BeerSearchFetcher
import quickbeer.android.domain.beerlist.store.BeerSearchStore

class BeerSearchRepository @Inject constructor(
    override val store: BeerSearchStore,
    fetcher: BeerSearchFetcher
) : ItemListRepository<String, Int, Beer, BeerJson>(store, fetcher) {

    override fun getLocalStream(key: String): Flow<List<Beer>> {
        // Usual exact match for the key, starting with empty list if no value is stored
        val exactMatch = super.getLocalStream(key)
            .onStart { emit(getLocal(key).orEmpty()) }
            .distinctUntilChanged()

        // Additional keyword matches from other searches
        val fuzzyMatch = store.search(key)

        return combine(exactMatch, fuzzyMatch) { a, b -> a + b }
            .map { it.distinctBy(Beer::id) }
    }
}
