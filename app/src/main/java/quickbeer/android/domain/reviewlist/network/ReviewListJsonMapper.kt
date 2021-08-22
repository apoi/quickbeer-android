package quickbeer.android.domain.reviewlist.network

import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.review.network.ReviewJsonMapper
import quickbeer.android.util.JsonMapper

class ReviewListJsonMapper<in K> : JsonMapper<K, List<Review>, List<ReviewJson>> {

    override fun map(key: K, source: List<ReviewJson>): List<Review> {
        return source.map { ReviewJsonMapper.map(it.id, it) }
    }
}
