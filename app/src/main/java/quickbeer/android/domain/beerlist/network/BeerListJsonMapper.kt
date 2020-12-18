package quickbeer.android.domain.beerlist.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.beer.network.BeerJsonMapper
import quickbeer.android.util.Mapper

object BeerListJsonMapper : Mapper<List<Beer>, List<BeerJson>> {

    override fun mapFrom(source: List<Beer>): List<BeerJson> {
        return source.map(BeerJsonMapper::mapFrom)
    }

    override fun mapTo(source: List<BeerJson>): List<Beer> {
        return source.map(BeerJsonMapper::mapTo)
    }
}
