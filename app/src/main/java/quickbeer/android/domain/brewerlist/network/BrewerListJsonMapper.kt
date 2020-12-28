package quickbeer.android.domain.brewerlist.network

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.brewer.network.BrewerJsonMapper
import quickbeer.android.util.Mapper

object BrewerListJsonMapper : Mapper<List<Brewer>, List<BrewerJson>> {

    override fun mapFrom(source: List<Brewer>): List<BrewerJson> {
        return source.map(BrewerJsonMapper::mapFrom)
    }

    override fun mapTo(source: List<BrewerJson>): List<Brewer> {
        return source.map(BrewerJsonMapper::mapTo)
    }
}
