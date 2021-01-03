package quickbeer.android.domain.brewerlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.domain.brewer.store.BrewerStoreCore
import quickbeer.android.domain.idlist.IdList

class BrewerSearchStore(
    indexStoreCore: StoreCore<String, IdList>,
    private val brewerStoreCore: BrewerStoreCore
) : BrewerListStore(QueryIndexMapper(), indexStoreCore, brewerStoreCore) {

    fun search(query: String) = brewerStoreCore.search(query)

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
            const val INDEX_PREFIX = "brewerSearch/"
        }
    }
}
