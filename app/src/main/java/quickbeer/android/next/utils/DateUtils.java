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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class DateUtils {

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    private DateUtils() {}

    public static DateTime value(@Nullable final DateTime date) {
        return date != null ? date : new DateTime();
    }

    public static int toDbValue(@Nullable final DateTime date) {
        return date != null
                ? (int) (date.getMillis() / 1000)
                : 0;
    }

    public static DateTime fromDbValue(int value) {
        return value > 0
                ? new DateTime((long) value * 1000)
                : null;
    }

    public static boolean isLater(@Nullable final DateTime first, @Nullable final DateTime second) {
        return first != null && (second == null || first.compareTo(second) > 0);
    }

    public static boolean isValidDate(@Nullable final DateTime date) {
        return date != null && date.isAfter(0);
    }
}
