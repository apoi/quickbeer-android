/**
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
package quickbeer.android.data.pojos.base

import io.reark.reark.pojo.OverwritablePojo
import org.threeten.bp.ZonedDateTime
import quickbeer.android.utils.DateUtils
import timber.log.Timber
import java.lang.reflect.Field

/**
 * Class to implement our specific json empty value definitions, to avoid overwriting
 * existing data with invalid values.
 */
abstract class Overwritable<T : OverwritablePojo<T>> : OverwritablePojo<T>() {

    override fun isEmpty(field: Field, pojo: OverwritablePojo<T>): Boolean {
        try {
            val value = field.get(pojo)

            return if (value == null) {
                true
            } else if (value is ZonedDateTime) {
                isEmpty(value)
            } else {
                super.isEmpty(field, pojo)
            }
        } catch (e: IllegalAccessException) {
            Timber.e(e, "Failed get at " + field.name)
        }

        return true
    }

    protected fun isEmpty(value: ZonedDateTime): Boolean {
        return !DateUtils.isValidDate(value)
    }

    override fun isEmpty(value: Int): Boolean {
        return value < 0
    }

    override fun isEmpty(value: Long): Boolean {
        return value < 0
    }

    override fun isEmpty(value: Double): Boolean {
        return value < 0
    }

    override fun isEmpty(value: Float): Boolean {
        return value < 0
    }
}