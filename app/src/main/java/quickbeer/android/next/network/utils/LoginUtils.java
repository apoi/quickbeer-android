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
package quickbeer.android.next.network.utils;

import java.net.CookieManager;
import java.net.HttpCookie;

import io.reark.reark.utils.Log;
import quickbeer.android.next.utils.StringUtils;

public class LoginUtils {
    private static final String TAG = LoginUtils.class.getSimpleName();

    private static final String USER_ID_KEY = "UserID";
    private static final String SESSION_ID_KEY = "SessionID";

    public static boolean hasLoginCookie(CookieManager cookieManager) {
        boolean userCookie = false;
        boolean sessionCookie = false;

        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            Log.v(TAG, "Cookie: " + cookie);

            if (!StringUtils.hasValue(cookie.getValue())) {
                continue;
            }

            if (USER_ID_KEY.equals(cookie.getName())) {
                userCookie = true;
            } else if (SESSION_ID_KEY.equals(cookie.getName())) {
                sessionCookie = true;
            }
        }

        Log.d(TAG, "Login status: userCookie: " + userCookie + ", sessionCookie: " + sessionCookie);
        return userCookie && sessionCookie;
    }

    public static String getUserId(CookieManager cookieManager) {
        for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            if (!StringUtils.hasValue(cookie.getValue())) {
                continue;
            }

            if (USER_ID_KEY.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return "";
    }
}
