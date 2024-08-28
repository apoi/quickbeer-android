package quickbeer.android.domain.placelist.network

import quickbeer.android.domain.place.Place
import quickbeer.android.domain.place.network.PlaceJson
import quickbeer.android.domain.place.network.PlaceJsonMapper
import quickbeer.android.util.JsonMapper

object PlaceListJsonMapper : JsonMapper<String, List<Place>, List<PlaceJson>> {

    override fun map(key: String, source: List<PlaceJson>): List<Place> {
        return source.map { PlaceJsonMapper.map(it.id, it) }
    }
}
