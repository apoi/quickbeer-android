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

import java.util.Date;

public class DateUtils {
    private DateUtils() {}

    public static Date value(Date date) {
        return date != null ? date : new Date();
    }

    public static int toDbValue(Date date) {
        return date != null
                ? (int) (date.getTime() / 1000)
                : 0;
    }

    public static Date fromDbValue(int value) {
        return value > 0
                ? new Date((long) value * 1000)
                : null;
    }

    public static boolean isLater(Date first, Date second) {
        if (first == null) {
            return false;
        } else if (second == null) {
            return true;
        } else {
            return first.compareTo(second) > 0;
        }
    }
}
