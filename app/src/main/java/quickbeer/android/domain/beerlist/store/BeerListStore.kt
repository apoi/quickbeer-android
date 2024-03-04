package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.data.store.store.SingleItemListStore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.store.BeerStoreCore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.inject.IdListPersistedCore

abstract class BeerListStore(
    indexPrefix: String,
    @IdListPersistedCore indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: BeerStoreCore
) : ItemListStore<String, Int, Beer>(
    indexMapper = QueryIndexMapper(indexPrefix),
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
) {

    private class QueryIndexMapper(private val indexPrefix: String) : IndexMapper<String> {

        override fun encode(index: String): String {
            return indexPrefix + index
        }

        override fun decode(value: String): String {
            return value.substring(indexPrefix.length)
        }

        override fun matches(value: String): Boolean {
            return value.startsWith(indexPrefix)
        }
    }
}

abstract class SingleBeerListStore(
    indexKey: String,
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: BeerStoreCore
) : SingleItemListStore<String, Int, Beer>(
    indexKey = indexKey,
    getKey = Beer::id,
    indexCore = indexStoreCore,
    valueCore = beerStoreCore
)
