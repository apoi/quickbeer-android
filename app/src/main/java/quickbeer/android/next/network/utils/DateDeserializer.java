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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class DateDeserializer implements JsonDeserializer<DateTime>, JsonSerializer<DateTime> {

    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    private static final DateTimeFormatter US_TIME_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss aa");

    private static final DateTimeFormatter US_DATE_FORMAT = DateTimeFormat.forPattern("MM/dd/yyyy");

    @Override
    public JsonElement serialize(DateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.toString(ISO_FORMAT));
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        final String date = json.getAsString();

        if (date.contains("T")) {
            return DateTime.parse(date, ISO_FORMAT);
        } else if (date.contains(" ")) {
            return DateTime.parse(date, US_TIME_FORMAT);
        } else {
            return DateTime.parse(date, US_DATE_FORMAT);
        }
    }
}
