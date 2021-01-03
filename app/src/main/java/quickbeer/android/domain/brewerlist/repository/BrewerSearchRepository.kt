package quickbeer.android.domain.brewerlist.repository

import kotlinx.coroutines.flow.Flow
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.brewerlist.network.BrewerSearchFetcher
import quickbeer.android.domain.brewerlist.store.BrewerSearchStore

class BrewerSearchRepository(
    override val store: BrewerSearchStore,
    fetcher: BrewerSearchFetcher
) : ItemListRepository<String, Int, Brewer, BrewerJson>(store, fetcher) {

    override fun getFuzzyLocalStream(key: String): Flow<List<Brewer>> {
        return store.search(key)
    }
}
