package quickbeer.android.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import quickbeer.android.Constants
import quickbeer.android.domain.login.LoginCookieJar
import quickbeer.android.network.HttpCode

/**
 * Interceptor to change 500 errors to 401 in case it's possible that a request failed due
 * to missing session. This is an ugly kludge to work around the API deficiencies.
 */
class AuthorizationErrorInterceptor(
    private val loginCookieJar: LoginCookieJar
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val isInternalError = response.code == HttpCode.INTERNAL_ERROR
        val isLoginRequest = request.url.encodedPath.endsWith(Constants.API_LOGIN_PAGE)
        val hasLoginCookie = loginCookieJar.hasUserCookie()

        if (isInternalError && !isLoginRequest && hasLoginCookie) {
            return response.newBuilder()
                .code(HttpCode.UNAUTHORIZED)
                .build()
        }

        return response
    }
}
