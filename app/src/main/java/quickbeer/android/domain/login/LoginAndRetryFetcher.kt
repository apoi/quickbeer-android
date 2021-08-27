package quickbeer.android.domain.login

import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.network.HttpCode
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.JsonMapper

/**
 * Fetcher that retries with a fresh login in case the fetch fails with 401.
 *
 * @param <K> Type of keys.
 * @param <V> Type of domain values.
 * @param <J> Type of result data, mostly Json objects.
 */
open class LoginAndRetryFetcher<in K, out V, J>(
    jsonMapper: JsonMapper<K, V, J>,
    api: suspend (K) -> ApiResult<J>,
    private val loginManager: LoginManager
) : Fetcher<K, V, J>(jsonMapper, api) {

    override suspend fun fetch(key: K): ApiResult<V> {
        val result = super.fetch(key)

        // Retry login for errors that were flagged as potentially unauthorized.
        // See [AuthorizationErrorInterceptor] for unauthorized call recognition.
        if (result is ApiResult.HttpError && result.code == HttpCode.UNAUTHORIZED) {
            val loginResult = loginManager.autoLogin()
            if (loginResult is ApiResult.Success) {
                return super.fetch(key)
            }
        }

        return result
    }
}
