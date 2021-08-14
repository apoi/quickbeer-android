package quickbeer.android.domain.user.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.user.User
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.first

class RateCountFetcher(
    api: RateBeerApi
) : Fetcher<Int, User, RateCountJson>(
    RateCountJsonMapper,
    { userId -> api.getUserRateCount(userId.toString()).first() }
)
