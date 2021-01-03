package quickbeer.android.domain.brewerlist.repository

import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.brewerlist.network.BrewerSearchFetcher
import quickbeer.android.domain.brewerlist.store.BrewerSearchStore

class BrewerSearchRepository(
    override val store: BrewerSearchStore,
    fetcher: BrewerSearchFetcher
) : ItemListRepository<String, Int, Brewer, BrewerJson>(store, fetcher) {

    fun searchLocal(query: String) = store.search(query)
}
