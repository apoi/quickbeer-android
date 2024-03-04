package quickbeer.android.domain.reviewlist.network

import quickbeer.android.domain.review.Review
import quickbeer.android.domain.review.network.UsersReviewJson
import quickbeer.android.domain.review.network.UsersReviewJsonMapper
import quickbeer.android.domain.user.User
import quickbeer.android.util.JsonMapper

class UsersReviewListJsonMapper : JsonMapper<Pair<User, Int>, List<Review>, List<UsersReviewJson>> {

    override fun map(key: Pair<User, Int>, source: List<UsersReviewJson>): List<Review> {
        return source.map { UsersReviewJsonMapper.map(key.first, it) }
    }
}
