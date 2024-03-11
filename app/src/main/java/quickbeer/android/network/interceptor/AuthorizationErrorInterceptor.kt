package quickbeer.android.network.interceptor

import okhttp3.Interceptor
import okhttp3.Request
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
        val hasLoginCookie = loginCookieJar.hasUserCookie()

        if (hasLoginCookie &&
            (isInternalError(request, response) || isEmptyReviewsResponse(request, response))
        ) {
            return response.newBuilder()
                .code(HttpCode.UNAUTHORIZED)
                .build()
        }

        return response
    }

    private fun isInternalError(request: Request, response: Response): Boolean {
        val isInternalError = response.code == HttpCode.INTERNAL_ERROR
        val isLoginRequest = request.url.encodedPath.startsWith(Constants.API_LOGIN_PAGE)
        return isInternalError && !isLoginRequest
    }

    /**
     * Bonus kludge: user reviews listing "fails" with 200 and an empty JSON list if login
     * isn't valid. Recognize this so that we can do a login and retry.
     */
    private fun isEmptyReviewsResponse(request: Request, response: Response): Boolean {
        // Only valid for the reviews endpoint
        val isReviewsRequest = request.url.encodedPath.startsWith(Constants.API_REVIEWS_PAGE)
        if (!isReviewsRequest) return false

        // Attempt to peek into the response body
        if (response.body == null) return false
        val peekedContent = response.peekBody(100).string().trim()

        // Reviews call without valid login is an empty JSON list. Could also mean no reviews
        // exists for this user, in which case we will auth & retry for nothing ¯\_(ツ)_/¯
        return peekedContent == "[]"
    }
}
