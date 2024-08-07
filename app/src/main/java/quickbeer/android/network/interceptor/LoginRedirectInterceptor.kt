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

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import quickbeer.android.Constants
import quickbeer.android.domain.login.LoginCookieJar
import quickbeer.android.network.HttpCode

class LoginRedirectInterceptor(private val cookieJar: LoginCookieJar) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return if (isLoginRequest(request)) {
            handleLoginResponse(chain)
        } else {
            chain.proceed(request)
        }
    }

    private fun isLoginRequest(request: Request): Boolean {
        return request.url.encodedPath.endsWith(Constants.API_LOGIN_PAGE)
    }

    private fun handleLoginResponse(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val contentType = response.body?.contentType()
        val body = response.body?.byteString()?.utf8()

        val code = when {
            isSuccessfulLogin(request, response, body) -> HttpCode.OK
            isKnownLoginFailure(body) -> HttpCode.FORBIDDEN
            else -> HttpCode.FORBIDDEN
        }

        return createResponse(response, contentType, body, code)
    }

    private fun isSuccessfulLogin(request: Request, response: Response, body: String?): Boolean {
        return if (response.code == HttpCode.OK &&
            request.url.encodedPath.endsWith(Constants.API_LOGIN_PAGE) &&
            hasLoginCookie(response, body)
        ) {
            // Success with id in a cookie header, or stored
            // cookie and user id in response body
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

    private fun hasLoginCookie(response: Response, body: String?): Boolean {
        val a = idFromStringList(response.headers("set-cookie")) != null
        val b = cookieJar.hasUserCookie()
        val c = idInResponseBody(body)

        return a || (b && c)
    }

    private fun idFromStringList(values: List<String>): String? {
        return values
            .asSequence()
            .mapNotNull { ID_HEADER_PATTERN.find(it)?.groupValues?.get(1) }
            .firstOrNull()
    }

    private fun idInResponseBody(body: String?): Boolean {
        if (body == null) return false
        return ID_BODY_PATTERN.containsMatchIn(body)
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

    companion object {
        private val ID_HEADER_PATTERN = "UserID=([0-9]+);".toRegex()
        private val ID_BODY_PATTERN = "^([0-9]+)$".toRegex(RegexOption.MULTILINE)
    }
}
