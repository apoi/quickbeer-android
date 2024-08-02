package quickbeer.android.domain.feed.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import quickbeer.android.R
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.domain.login.LoginFirstFetcher
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.inject.HtmlPreservingApi
import quickbeer.android.inject.HtmlPreservingMoshi
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.ResourceProvider

// Note that HTML needs to be preserved for parsing the links
class FeedFetcher(
    @HtmlPreservingApi api: RateBeerApi,
    loginManager: LoginManager
) : LoginFirstFetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode -> api.getFeed(mode) },
    loginManager = loginManager
)

// For debug purposes
class FeedResourcesFetcher(
    resourceProvider: ResourceProvider,
    @HtmlPreservingMoshi moshi: Moshi
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
