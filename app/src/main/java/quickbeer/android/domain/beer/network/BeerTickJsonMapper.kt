package quickbeer.android.domain.beer.network

import okhttp3.ResponseBody
import quickbeer.android.util.JsonMapper

object BeerTickJsonMapper : JsonMapper<BeerTickFetcher.TickKey, Int, ResponseBody> {

    override fun map(key: BeerTickFetcher.TickKey, source: ResponseBody): Int {
        return key.beerId
    }
}
