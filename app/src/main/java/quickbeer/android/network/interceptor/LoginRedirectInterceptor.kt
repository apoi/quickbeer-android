/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.network.interceptor

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class LoginRedirectInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (isLoginRequest(request)) {
            handleLoginResponse(chain)
        } else chain.proceed(request)
    }

    private fun isLoginRequest(request: Request): Boolean {
        return request.url.encodedPath.endsWith(LoginUtils.SIGN_IN_PAGE)
    }

    private fun handleLoginResponse(
        chain: Interceptor.Chain
    ): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val contentType = response.body?.contentType()
        val body = response.body?.byteString()?.utf8()

        val code = when {
            isSuccessfulLogin(request, response) -> LoginUtils.HTTP_OK
            isKnownLoginFailure(body) -> LoginUtils.HTTP_FORBIDDEN
            else -> LoginUtils.HTTP_FORBIDDEN
        }

        return createResponse(response, contentType, body, code)
    }

    private fun isSuccessfulLogin(request: Request, response: Response): Boolean {
        return if (response.code == LoginUtils.HTTP_OK &&
            request.url.encodedPath.endsWith(LoginUtils.SIGN_IN_PAGE) &&
            LoginUtils.idFromStringList(response.headers("set-cookie")) != null
        ) {
            // Success with id in a cookie header
            true
        } else {
            // Old-style API redirect response
            response.isRedirect &&
                response.header("location")?.contains("uid") == true
        }
    }

    private fun isKnownLoginFailure(body: String?): Boolean {
        return body?.contains("failed login") == true
    }

    /**
     * Body contents can be read only once from the response, so we must create
     * a new response object; otherwise attempts to access the body contents
     * later in the chain would throw.
     */
    private fun createResponse(
        response: Response,
        contentType: MediaType?,
        body: String?,
        code: Int
    ): Response {
        return response.newBuilder()
            .body(body?.toResponseBody(contentType))
            .code(code)
            .build()
    }
}
