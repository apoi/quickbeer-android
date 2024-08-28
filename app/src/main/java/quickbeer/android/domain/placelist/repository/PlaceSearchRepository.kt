package quickbeer.android.domain.placelist.repository

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import quickbeer.android.data.repository.repository.ItemListRepository
import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.network.PlaceJson
import quickbeer.android.domain.placelist.network.PlaceSearchFetcher
import quickbeer.android.domain.placelist.store.PlaceSearchStore

class PlaceSearchRepository @Inject constructor(
    override val store: PlaceSearchStore,
    fetcher: PlaceSearchFetcher
) : ItemListRepository<String, Int, Place, PlaceJson>(store, fetcher) {

    override fun getLocalStream(key: String): Flow<List<Place>> {
        // Usual exact match for the key, starting with empty list if no value is stored
        val exactMatch = super.getLocalStream(key)
            .onStart { emit(getLocal(key).orEmpty()) }
            .distinctUntilChanged()

        // Additional keyword matches from other searches
        val fuzzyMatch = store.search(key)

        return combine(exactMatch, fuzzyMatch) { a, b -> a + b }
            .map { it.distinctBy(Place::id) }
    }
}
