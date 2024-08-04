package quickbeer.android.domain.place.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.place.Place
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.first

class PlaceFetcher(
    api: RateBeerApi
) : Fetcher<Int, Place, PlaceJson>(
    jsonMapper = PlaceJsonMapper,
    api = { key -> api.getPlace(key).first() }
)
