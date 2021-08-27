package quickbeer.android.domain.login

import com.franmontiel.persistentcookiejar.cache.CookieCache
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrl
import quickbeer.android.Constants
import quickbeer.android.network.cookie.SessionPersistingCookieJar

/**
 * Cookie jar that knows about service login state.
 */
class LoginCookieJar(
    cache: CookieCache,
    persistor: CookiePersistor
) : SessionPersistingCookieJar(cache, persistor) {

    fun hasUserCookie(): Boolean {
        return getUserId() != null
    }

    fun getUserId(): Int? {
        return loadForRequest(Constants.API_URL.toHttpUrl())
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
