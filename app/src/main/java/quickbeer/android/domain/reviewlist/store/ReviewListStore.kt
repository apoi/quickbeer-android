package quickbeer.android.domain.reviewlist.store

import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.ItemListStore
import quickbeer.android.domain.idlist.IdList
import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.store.ReviewStoreCore

abstract class ReviewListStore(
    indexPrefix: String,
    indexStoreCore: StoreCore<String, IdList>,
    reviewStoreCore: ReviewStoreCore
) : ItemListStore<String, Int, Review>(
    indexMapper = QueryIndexMapper(indexPrefix),
    getKey = Review::id,
    indexCore = indexStoreCore,
    valueCore = reviewStoreCore
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
