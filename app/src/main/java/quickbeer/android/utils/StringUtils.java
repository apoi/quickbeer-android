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
package quickbeer.android.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final Pattern BASE_GLYPH = Pattern.compile("\\p{M}");

    private StringUtils() {}

    public static boolean hasValue(@Nullable String value) {
        return value != null && !value.isEmpty();
    }

    public static boolean isEmpty(@Nullable String value) {
        return value == null || value.isEmpty();
    }

    public static String value(@Nullable String primary, @NonNull String secondary) {
        return hasValue(primary) ? primary : secondary;
    }

    public static String value(@Nullable String value) {
        return value != null ? value : "";
    }

    public static boolean equals(@Nullable String first, @Nullable String second) {
        return first == null
                ? second == null
                : first.equals(second);
    }

    @NonNull
    public static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        normalized = BASE_GLYPH.matcher(normalized).replaceAll("");

        return normalized;
    }

    @NonNull
    public static String removeTrailingSlash(@Nullable String value) {
        if (value == null) {
            return "";
        }

        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }

}
