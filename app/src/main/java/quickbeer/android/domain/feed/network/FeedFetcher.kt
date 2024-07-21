package quickbeer.android.domain.feed.network

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedDataItem
import quickbeer.android.network.RateBeerApi
import timber.log.Timber

class FeedFetcher(
    api: RateBeerApi
) : Fetcher<String, List<FeedDataItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode ->
        Timber.d("FETCH FEED $mode")
        api.getFeed(mode)
    }
)
