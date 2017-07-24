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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ix.Ix;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import polanski.option.Option;
import quickbeer.android.Constants;

import static polanski.option.Option.ofObj;

public final class LoginUtils {

    private static final String USER_ID_KEY = "UserID";

    private static final Pattern ID_PATTERN = Pattern.compile("UserID=([0-9]+);");

    @NonNull
    public static Option<Integer> getUserId(@NonNull CookieJar cookieJar) {
        return ofObj(cookieJar)
                .map(jar -> jar.loadForRequest(HttpUrl.parse(Constants.API_URL)))
                .flatMap(LoginUtils::idFromCookieList)
                .map(Integer::parseInt);
    }

    @NonNull
    private static Option<String> idFromCookieList(@NonNull List<Cookie> cookies) {
        return Ix.from(cookies)
                .filter(cookie -> Objects.equals(cookie.name(), USER_ID_KEY))
                .map(Cookie::value)
                .map(Option::ofObj)
                .first(Option.none());
    }

    @NonNull
    public static Option<String> idFromStringList(@NonNull List<String> values) {
        return Ix.from(values)
                .filter(value -> value.contains(USER_ID_KEY))
                .map(value -> {
                    Matcher matcher = ID_PATTERN.matcher(value);
                    return matcher.find() ? ofObj(matcher.group(1)) : Option.<String>none();
                })
                .first();
    }
}
