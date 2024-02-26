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
    BeerTickJsonMapper,
    tickAction(api),
    loginManager
) {

    // Wrapper class for tick request parameters
    data class TickKey(val beerId: Int, val userId: Int, val tick: Int)

    companion object {

        private const val MODE_TICK = 2
        private const val MODE_REMOVE = 3

        // Creator method for tick action
        fun tickAction(api: RateBeerApi): suspend (TickKey) -> ApiResult<ResponseBody> {
            return { (beerId, userId, tick) ->
                if (tick == 0) {
                    api.tickBeer(beerId, userId, MODE_REMOVE, null)
                } else {
                    api.tickBeer(beerId, userId, MODE_TICK, tick)
                }
            }
        }
    }
}
