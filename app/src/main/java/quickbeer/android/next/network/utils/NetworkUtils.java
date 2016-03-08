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

import java.util.HashMap;
import java.util.Map;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.utils.ApiKey;

public class NetworkUtils {

    private static String apiKey;

    public static Map<String, String> createRequestParams(String key, String value) {
        if (apiKey == null) {
            initApiKey();
        }

        Map<String, String> map = new HashMap<>();
        map.put("k", apiKey);
        map.put(key, value);

        return map;
    }

    private static void initApiKey() {
        apiKey = ApiKey.getApiKey(QuickBeer.getInstance().getApplicationContext());
    }
}
