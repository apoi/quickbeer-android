package quickbeer.android.domain.login

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.JsonMapper

/**
 * Fetcher that always logs in first before executing the actual request. This is not optimal, but
 * is a functional workaround for friends feed behaviour where request simply timeouts if there's
 * no valid login session.
 *
 * @param <K> Type of keys.
 * @param <V> Type of domain values.
 * @param <J> Type of result data, mostly Json objects.
 */
open class LoginFirstFetcher<in K : Any, out V : Any, J : Any>(
    jsonMapper: JsonMapper<K, V, J>,
    api: suspend (K) -> ApiResult<J>,
    private val loginManager: LoginManager
) : Fetcher<K, V, J>(jsonMapper, api) {

    override suspend fun fetch(key: K): ApiResult<V> {
        return when (val loginResult = loginManager.autoLogin()) {
            is ApiResult.Success -> super.fetch(key)
            is ApiResult.HttpError -> ApiResult.HttpError(loginResult.code, loginResult.cause)
            is ApiResult.NetworkError -> ApiResult.NetworkError(loginResult.cause)
            is ApiResult.UnknownError -> ApiResult.UnknownError(loginResult.cause)
        }
    }
}
