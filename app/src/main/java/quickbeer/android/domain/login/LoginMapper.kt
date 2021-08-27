package quickbeer.android.domain.login

import javax.inject.Inject
import okhttp3.ResponseBody
import quickbeer.android.util.JsonMapper

class LoginMapper @Inject constructor(
    private val loginCookieJar: LoginCookieJar
) : JsonMapper<Pair<String, String>, Int, ResponseBody> {

    override fun map(key: Pair<String, String>, source: ResponseBody): Int {
        // User ID was extracted from headers with interceptor
        return loginCookieJar.getUserId()
            ?: error("Failed to process login result")
    }
}
