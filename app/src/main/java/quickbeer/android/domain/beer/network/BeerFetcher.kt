package quickbeer.android.domain.beer.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.beer.Beer
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.first

class BeerFetcher(api: RateBeerApi) :
    Fetcher<Int, Beer, BeerJson>(BeerJsonMapper, { key -> api.getBeer(key).first() })
