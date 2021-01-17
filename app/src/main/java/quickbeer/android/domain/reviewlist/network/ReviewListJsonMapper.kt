package quickbeer.android.domain.reviewlist.network

import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.review.network.ReviewJsonMapper
import quickbeer.android.util.Mapper

object ReviewListJsonMapper : Mapper<List<Review>, List<ReviewJson>> {

    override fun mapFrom(source: List<Review>): List<ReviewJson> {
        return source.map(ReviewJsonMapper::mapFrom)
    }

    override fun mapTo(source: List<ReviewJson>): List<Review> {
        return source.map(ReviewJsonMapper::mapTo)
    }
}
