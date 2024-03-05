package quickbeer.android.domain.ratinglist.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.RatingJson
import quickbeer.android.domain.rating.network.RatingJsonMapper
import quickbeer.android.util.JsonMapper

class RatingListJsonMapper<in K> : JsonMapper<K, List<Rating>, List<RatingJson>> {

    override fun map(key: K, source: List<RatingJson>): List<Rating> {
        return source.map { RatingJsonMapper.map(it.id, it) }
    }
}
