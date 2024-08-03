package quickbeer.android.domain.feed.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import quickbeer.android.R
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.domain.feed.FeedItem
import quickbeer.android.domain.login.LoginManager
import quickbeer.android.inject.HtmlPreservingApi
import quickbeer.android.inject.HtmlPreservingMoshi
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.ResourceProvider

/**
 * Fetcher that logs in first when fetching friends feed. This is not optimal, but is a functional
 * workaround for friends feed behaviour where request simply timeouts if there's no valid login
 * session.
 *
 * Note that HTML needs to be preserved for parsing the links.
 */
class FeedFetcher(
    @HtmlPreservingApi api: RateBeerApi,
    private val loginManager: LoginManager
) : Fetcher<String, List<FeedItem>, List<FeedItemJson>>(
    jsonMapper = FeedItemListJsonMapper,
    api = { mode -> api.getFeed(mode) }
) {
    override suspend fun fetch(mode: String): ApiResult<List<FeedItem>> {
        if (mode != MODE_FRIENDS) return super.fetch(mode)

        return when (val loginResult = loginManager.autoLogin()) {
            is ApiResult.Success -> super.fetch(mode)
            is ApiResult.HttpError -> ApiResult.HttpError(loginResult.code, loginResult.cause)
            is ApiResult.NetworkError -> ApiResult.NetworkError(loginResult.cause)
            is ApiResult.UnknownError -> ApiResult.UnknownError(loginResult.cause)
        }
    }

    companion object {
        private const val MODE_FRIENDS = "0"
    }
}

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
