/**
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.data.columns

import net.simonvt.schematic.annotation.DataType
import net.simonvt.schematic.annotation.PrimaryKey

interface BeerMetadataColumns {
    companion object {
        @DataType(DataType.Type.INTEGER)
        @PrimaryKey
        const val ID = "beer_id"

        @DataType(DataType.Type.INTEGER)
        const val UPDATED = "updated" // Date of the last data fetch

        @DataType(DataType.Type.INTEGER)
        const val ACCESSED = "accessed" // Date of the last access

        @DataType(DataType.Type.INTEGER)
        const val REVIEW_ID = "review_id" // Own review id

        @DataType(DataType.Type.INTEGER)
        const val MODIFIED = "modified" // Flag if unsaved modifications
    }
}