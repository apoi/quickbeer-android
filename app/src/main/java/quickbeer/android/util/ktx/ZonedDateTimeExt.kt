/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.util.ktx

import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.temporal.ChronoUnit

fun ZonedDateTime?.orEpoch(): ZonedDateTime {
    return if (isValidDate()) {
        this!!
    } else {
        ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneOffset.UTC)
    }
}

fun ZonedDateTime?.isValidDate(): Boolean {
    return this != null && this.toEpochSecond() > 0
}

fun ZonedDateTime?.within(millis: Long): Boolean {
    val compare = ZonedDateTime.now().minus(millis, ChronoUnit.MILLIS)
    return this != null && this > compare
}

fun ZonedDateTime.isOlderThan(duration: Duration): Boolean {
    return (this + duration) < ZonedDateTime.now()
}

fun ZonedDateTime?.formatDateTime(template: String): String {
    val localTime = orEpoch().withZoneSameInstant(ZoneId.systemDefault())
    return String.format(
        template,
        localTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)),
        localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    )
}

fun ZonedDateTime?.formatDate(): String {
    return orEpoch()
        .withZoneSameInstant(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

fun ZonedDateTime?.orLater(other: ZonedDateTime?): ZonedDateTime? {
    return when {
        this == null -> other
        other == null -> this
        this > other -> this
        else -> other
    }
}
