package quickbeer.android.domain.beerlist.network

import quickbeer.android.domain.beer.Beer
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.login.LoginAndRetryFetcher
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.network.RateBeerApi

class TickedBeersFetcher(
    api: RateBeerApi,
    loginManager: LoginManager
) : LoginAndRetryFetcher<String, List<Beer>, List<BeerJson>>(
    BeerListJsonMapper(), { userId -> api.getTicks(userId) }, loginManager
)
