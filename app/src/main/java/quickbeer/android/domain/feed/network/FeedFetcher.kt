package quickbeer.android.domain.feed.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import quickbeer.android.R
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.ResourceProvider

class FeedFetcher(
    api: RateBeerApi
) : Fetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode -> api.getFeed(mode) }
)

// For debug purposes
class FeedResourcesFetcher(
    resourceProvider: ResourceProvider,
    moshi: Moshi
) : Fetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { _ ->
        val listType = Types.newParameterizedType(List::class.java, FeedItemJson::class.java)
        val adapter = moshi.adapter<List<FeedItemJson>>(listType)
        val json = resourceProvider.getRaw(R.raw.feed)
        val feedItems = adapter.fromJson(json)
        ApiResult.Success(feedItems)
    }
)
