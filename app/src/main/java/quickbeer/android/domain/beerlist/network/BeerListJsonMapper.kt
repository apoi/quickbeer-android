package quickbeer.android.domain.beerlist.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beer.network.BeerJsonMapper
import quickbeer.android.util.JsonMapper

class BeerListJsonMapper<in K> : JsonMapper<K, List<Beer>, List<BeerJson>> {

    override fun map(key: K, source: List<BeerJson>): List<Beer> {
        return source.map { BeerJsonMapper.map(it.id, it) }
    }
}
