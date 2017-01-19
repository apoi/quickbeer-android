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
package quickbeer.android.data.pojos.base;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import java.lang.reflect.Field;

import io.reark.reark.pojo.OverwritablePojo;
import io.reark.reark.utils.Log;

/**
 * Class to implement our specific json empty value definitions, to avoid overwriting
 * existing data with invalid values.
 */
public abstract class OverwritableBuilder<T extends OverwritablePojo<T>> extends OverwritablePojo<T> {

    private static final String TAG = OverwritableBuilder.class.getSimpleName();

    @Override
    protected boolean isEmpty(@NonNull final Field field, @NonNull final OverwritablePojo<T> pojo) {
        try {
            Object value = field.get(pojo);

            if (value == null) {
                return true;
            } else if (value instanceof DateTime) {
                return isEmpty((DateTime) value);
            } else {
                return super.isEmpty(field, pojo);
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Failed get at " + field.getName(), e);
        }

        return true;
    }

    protected boolean isEmpty(DateTime value) {
        return value.getMillis() <= 0;
    }

    @Override
    protected boolean isEmpty(int value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(long value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(double value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(float value) {
        return value == 0;
    }

    /**
     * The AutoValue builder class does not create a proper equals method. We can instead claim
     * to be always non-equal, as this is for builder class equality only; the equality of the
     * actual pojos was verified before invoking the overwriting through merge.
     */
    @Override
    public boolean equals(Object o) {
        return false;
    }
}
