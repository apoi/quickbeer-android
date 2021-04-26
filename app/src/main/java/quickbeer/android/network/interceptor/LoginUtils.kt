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

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl.Companion.toHttpUrl
import quickbeer.android.Constants

object LoginUtils {

    private const val USER_ID_KEY = "UserID"

    private val ID_PATTERN = "UserID=([0-9]+);".toRegex()

    fun getUserId(cookieJar: CookieJar): Int? {
        return cookieJar.loadForRequest(Constants.API_URL.toHttpUrl())
            .let(::idFromCookieList)
            ?.toInt()
    }

    private fun idFromCookieList(cookies: List<Cookie>): String? {
        return cookies
            .firstOrNull { it.name == USER_ID_KEY }
            ?.value
    }

    fun idFromStringList(values: List<String>): String? {
        return values
            .asSequence()
            .mapNotNull { ID_PATTERN.find(it)?.groupValues?.get(1) }
            .firstOrNull()
    }
}
