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
package quickbeer.android.providers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import java.io.InputStream;

import static io.reark.reark.utils.Preconditions.get;

public class ResourceProvider {

    @NonNull
    private final Context context;

    public ResourceProvider(@NonNull Context context) {
        this.context = get(context);
    }

    @NonNull
    public String getString(@StringRes final int id) {
        return context.getString(id);
    }

    public int getDrawableIdentifier(@NonNull String name) {
        return context.getResources().getIdentifier(get(name), "drawable", context.getPackageName());
    }

    @NonNull
    public InputStream openRawResource(@RawRes final int id) {
        return context.getResources().openRawResource(id);
    }

}
