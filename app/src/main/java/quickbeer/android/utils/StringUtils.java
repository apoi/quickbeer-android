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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import polanski.option.Option;

import java.text.Normalizer;
import java.util.concurrent.Callable;
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

    @NonNull
    public static String value(@Nullable String value) {
        return value != null ? value : "";
    }

    @NonNull
    public static Option<String> emptyAsNone(@Nullable String value) {
        return hasValue(value)
                ? Option.ofObj(value)
                : Option.none();
    }

    @NonNull
    public static Option<String> emptyAsNone(@NonNull Callable<String> callable) {
        try {
            return emptyAsNone(callable.call());
        } catch (Exception ignored) {
            return Option.none();
        }
    }

    @NonNull
    public static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }

        //noinspection DynamicRegexReplaceableByCompiledPattern
        return BASE_GLYPH.matcher(Normalizer.normalize(value, Normalizer.Form.NFD))
                .replaceAll("")
                .replaceAll("æ", "ae")
                .replaceAll("Æ", "AE")
                .replaceAll("ß", "ss")
                .replaceAll("ø", "o")
                .replaceAll("Ø", "O");
    }

    @NonNull
    public static String addMissingProtocol(@Nullable String value) {
        if (value == null) {
            return "";
        }

        if (!value.startsWith("http")) {
            return "http://" + value;
        }

        return value;
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
