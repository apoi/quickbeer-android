package quickbeer.android.domain.feed.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.network.RateBeerApi

class FeedFetcher(
    api: RateBeerApi
) : Fetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode -> api.getFeed(mode) }
)
