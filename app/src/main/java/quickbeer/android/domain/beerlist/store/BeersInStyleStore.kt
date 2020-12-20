package quickbeer.android.domain.beerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.idlist.IdList

class BeersInStyleStore(
    indexStoreCore: StoreCore<String, IdList>,
    beerStoreCore: StoreCore<Int, Beer>
) : BeerListStore(QueryIndexMapper(), indexStoreCore, beerStoreCore) {

    private class QueryIndexMapper : IndexMapper<String> {

        override fun encode(index: String): String {
            return INDEX_PREFIX + index
        }

        override fun decode(value: String): String {
            return value.substring(INDEX_PREFIX.length)
        }

        override fun matches(value: String): Boolean {
            return value.startsWith(INDEX_PREFIX)
        }

        companion object {
            const val INDEX_PREFIX = "style/"
        }
    }
}
