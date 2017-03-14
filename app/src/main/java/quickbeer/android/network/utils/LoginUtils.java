/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.network.utils;

import android.support.annotation.NonNull;

import java.util.List;

import ix.Ix;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import polanski.option.Option;
import quickbeer.android.Constants;

public final class LoginUtils {

    private static final String USER_ID_KEY = "UserID";

    public static Option<Integer> getUserId(@NonNull CookieJar cookieJar) {
        return Option.ofObj(cookieJar)
                .map(jar -> jar.loadForRequest(HttpUrl.parse(Constants.API_URL)))
                .flatMap(LoginUtils::idFromCookieList)
                .map(Integer::parseInt);
    }

    private static Option<String> idFromCookieList(@NonNull List<Cookie> cookies) {
        return Ix.from(cookies)
                .filter(cookie -> cookie.name().equals(USER_ID_KEY))
                .map(Cookie::value)
                .map(Option::ofObj)
                .first(Option.none());
    }

}
