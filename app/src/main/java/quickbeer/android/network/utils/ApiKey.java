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

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import quickbeer.android.R;
import timber.log.Timber;

public class ApiKey {

    public String getApiKey(@NonNull final Context context) {
        try {
            // RateBeer API keys may not be shared. You'll need to acquire your own key.
            // Store the key as plain text in the file app/src/main/res/raw/apikey.txt.
            InputStream stream = context.getResources().openRawResource(R.raw.apikey);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String value = reader.readLine();
            if (value == null || value.trim().isEmpty()) {
                Timber.e("Invalid API key!");
            }

            return value;
        } catch (IOException e) {
            return null;
        }
    }
}
