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

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class DateUtils {

    private static final ZonedDateTime EPOCH =
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneOffset.UTC);

    private DateUtils() {}

    @NonNull
    public static ZonedDateTime value(@Nullable ZonedDateTime date) {
        return isValidDate(date) ? date : EPOCH;
    }

    @NonNull
    public static String format(@Nullable ZonedDateTime date) {
        return value(date).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Nullable
    public static ZonedDateTime fromEpochSecond(int value) {
        return value > 0
                ? ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneOffset.UTC)
                : null;
    }

    public static int toEpochSecond(@Nullable ZonedDateTime date) {
        return isValidDate(date)
                ? (int) date.toEpochSecond()
                : 0;
    }

    public static boolean isValidDate(@Nullable ZonedDateTime date) {
        return date != null && date.isAfter(EPOCH);
    }
}
