package quickbeer.android.domain.feed.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.network.RateBeerApi
import timber.log.Timber

class FeedFetcher(
    api: RateBeerApi
) : Fetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode ->
        Timber.d("FETCH FEED $mode")
        api.getFeed(mode)
    }
)
