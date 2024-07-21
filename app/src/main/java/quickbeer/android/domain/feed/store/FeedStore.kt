package quickbeer.android.domain.feed.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.feed.FeedDataItem
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.inject.IdListMemoryCore

class FeedStore @Inject constructor(
    @IdListMemoryCore indexStoreCore: StoreCore<String, IdList>,
    feedDataItemStoreCore: StoreCore<Int, FeedDataItem>
) : ItemListStore<String, Int, FeedDataItem>(
    indexMapper = FeedModeMapper(),
    getKey = FeedDataItem::id,
    indexCore = indexStoreCore,
    valueCore = feedDataItemStoreCore
) {

    private class FeedModeMapper : IndexMapper<String> {

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
            const val INDEX_PREFIX = "feed/"
        }
    }
}
