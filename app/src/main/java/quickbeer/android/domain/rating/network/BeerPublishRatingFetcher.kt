package quickbeer.android.domain.rating.network

import okhttp3.ResponseBody
import quickbeer.android.domain.login.LoginAndRetryFetcher
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.domain.rating.Rating
import quickbeer.android.network.RateBeerApi

class BeerPublishRatingFetcher(
    api: RateBeerApi,
    loginManager: LoginManager
) : LoginAndRetryFetcher<Rating, Boolean, ResponseBody>(
    SuccessResponseMapper(),
    { rating ->
        if (!rating.isValid()) error("Incomplete rating")

        api.postRating(
            beerId = rating.beerId ?: error("Missing rating"),
            appearance = rating.appearance ?: error("Missing appearance"),
            aroma = rating.aroma ?: error("Missing aroma"),
            flavor = rating.flavor ?: error("Missing flavor"),
            palate = rating.mouthfeel ?: error("Missing palate"),
            overall = rating.overall ?: error("Missing overall"),
            comments = rating.comments ?: error("Missing comments")
        )
    },
    loginManager
)
