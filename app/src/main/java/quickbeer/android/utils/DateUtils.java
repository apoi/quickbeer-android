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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");

    private DateUtils() {}

    @NonNull
    public static DateTime value(@Nullable DateTime date) {
        return isValidDate(date) ? date : new DateTime(0);
    }

    @NonNull
    public static String format(@Nullable DateTime date) {
        return value(date).toString(DATE_FORMAT);
    }

    @Nullable
    public static DateTime fromDbValue(int value) {
        return value > 0
                ? new DateTime((long) value * 1000)
                : null;
    }

    public static int toDbValue(@Nullable DateTime date) {
        return isValidDate(date)
                ? (int) (date.getMillis() / 1000)
                : 0;
    }

    public static boolean isValidDate(@Nullable DateTime date) {
        return date != null && date.isAfter(0);
    }
}
