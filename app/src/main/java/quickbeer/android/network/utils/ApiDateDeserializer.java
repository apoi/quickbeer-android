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
package quickbeer.android.network.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;
import java.util.Locale;

public class ApiDateDeserializer implements JsonDeserializer<ZonedDateTime>, JsonSerializer<ZonedDateTime> {

    private static final ZoneId SERVER_ZONE =
            ZoneId.ofOffset("", ZoneOffset.ofHours(-5));

    private static final DateTimeFormatter ISO_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                    .withZone(ZoneOffset.UTC);

    private static final DateTimeFormatter US_DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy h:mm:ss a")
                    .withLocale(Locale.US)
                    .withZone(SERVER_ZONE);

    private static final DateTimeFormatter US_DATE_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    @Override
    public JsonElement serialize(ZonedDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(ISO_FORMAT));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final String date = json.getAsString();

        if (date.contains("T")) {
            return ZonedDateTime.parse(date, ISO_FORMAT);
        } else if (date.contains(" ")) {
            return ZonedDateTime.parse(date, US_DATETIME_FORMAT)
                    .withZoneSameInstant(ZoneOffset.UTC);
        } else {
            return LocalDate.parse(date, US_DATE_FORMAT)
                    .atStartOfDay(ZoneOffset.UTC);
        }
    }

}
