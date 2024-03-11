package quickbeer.android.domain.ratinglist.network

import quickbeer.android.domain.rating.Rating
import quickbeer.android.domain.rating.network.BeerRatingJson
import quickbeer.android.domain.rating.network.BeerRatingJsonMapper
import quickbeer.android.util.JsonMapper

class RatingListJsonMapper<in K> : JsonMapper<K, List<Rating>, List<BeerRatingJson>> {

    override fun map(key: K, source: List<BeerRatingJson>): List<Rating> {
        return source.map { BeerRatingJsonMapper.map(it.id, it) }
    }
}
