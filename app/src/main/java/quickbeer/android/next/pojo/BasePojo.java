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
package quickbeer.android.next.pojo;

import io.reark.reark.pojo.OverwritablePojo;

/**
 * Class to implement our specific json empty value definitions, to avoid overwriting
 * existing data with invalid values.
 */
public abstract class BasePojo<T extends OverwritablePojo> extends OverwritablePojo<T> {
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
}
