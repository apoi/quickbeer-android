package quickbeer.android.domain.login

import okhttp3.ResponseBody
import quickbeer.android.data.fetcher.Fetcher
import quickbeer.android.network.RateBeerApi
import quickbeer.android.network.result.ApiResult

class LoginFetcher(
    api: RateBeerApi,
    loginMapper: LoginMapper
) : Fetcher<Pair<String, String>, Int, ResponseBody>(
    jsonMapper = loginMapper,
    api = { (username, password) -> api.login(username, password, DEFAULT_SAVEINFO) }
) {

    suspend fun fetch(username: String, password: String): ApiResult<Int> {
        return fetch(username to password)
    }

    companion object {
        private const val DEFAULT_SAVEINFO = "on"
    }
}
