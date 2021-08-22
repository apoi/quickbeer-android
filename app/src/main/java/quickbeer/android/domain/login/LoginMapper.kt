package quickbeer.android.domain.login

import javax.inject.Inject
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody
import quickbeer.android.Constants
import quickbeer.android.util.JsonMapper

class LoginMapper @Inject constructor(
    private val cookieJar: CookieJar
) : JsonMapper<Pair<String, String>, Int, ResponseBody> {

    override fun map(key: Pair<String, String>, source: ResponseBody): Int {
        // User ID was extracted from headers with interceptor
        return getUserId(cookieJar)
            ?: error("Failed to process login result")
    }

    private fun getUserId(cookieJar: CookieJar): Int? {
        return cookieJar.loadForRequest(Constants.API_URL.toHttpUrl())
            .let(::idFromCookieList)
            ?.toInt()
    }

    private fun idFromCookieList(cookies: List<Cookie>): String? {
        return cookies
            .firstOrNull { it.name == USER_ID_KEY }
            ?.value
    }

    companion object {
        private const val USER_ID_KEY = "UserID"
    }
}
