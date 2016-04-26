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
package quickbeer.android.next.utils;

public class StringUtils {
    private StringUtils() {}

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static String value(String primary, String secondary) {
        return hasValue(primary) ? primary : secondary;
    }

    public static String value(String value) {
        return value != null ? value : "";
    }

    public static boolean equals(String first, String second) {
        return first == null
                ? second == null
                : first.equals(second);
    }
}
