package quickbeer.android.domain.brewerlist.network

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.brewer.network.BrewerJsonMapper
import quickbeer.android.util.JsonMapper

object BrewerListJsonMapper : JsonMapper<String, List<Brewer>, List<BrewerJson>> {

    override fun map(key: String, source: List<BrewerJson>): List<Brewer> {
        return source.map { BrewerJsonMapper.map(it.id, it) }
    }
}
