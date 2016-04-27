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

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reark.reark.utils.Log;

public class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final String TAG = DateDeserializer.class.getSimpleName();

    private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);
    private static final DateFormat US_TIME_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa", Locale.ROOT);
    private static final DateFormat US_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.ROOT);

    @Override
    public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(ISO_FORMAT.format(date));
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String date = json.getAsString();

        try {
            if (date.contains("T")) {
                return ISO_FORMAT.parse(date);
            } else if (date.contains(" ")) {
                return US_TIME_FORMAT.parse(date);
            } else {
                return US_DATE_FORMAT.parse(date);
            }
        } catch (ParseException e) {
            Log.e(TAG, "error parsing " + date, e);
            return new Date();
        }
    }
}
