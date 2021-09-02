package quickbeer.android.domain.beer.network

import okhttp3.ResponseBody
import quickbeer.android.domain.login.LoginAndRetryFetcher
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult

class BeerTickFetcher(
    api: RateBeerApi,
    loginManager: LoginManager
) : LoginAndRetryFetcher<BeerTickFetcher.TickKey, Int, ResponseBody>(
    BeerTickJsonMapper, tickAction(api), loginManager
) {

    // Wrapper class for tick request parameters
    data class TickKey(val beerId: Int, val userId: Int, val tick: Int)

    companion object {

        // Creator method for tick action
        fun tickAction(
            api: RateBeerApi
        ): suspend (TickKey) -> ApiResult<ResponseBody> {
            return { (beerId, userId, tick) ->
                if (tick == 0) {
                    api.tickBeer(beerId, userId, 3, null)
                } else {
                    api.tickBeer(beerId, userId, 2, tick)
                }
            }
        }
    }
}
