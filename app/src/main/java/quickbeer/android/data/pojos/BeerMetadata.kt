/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.data.pojos

import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.pojos.base.Overwritable

data class BeerMetadata(val beerId: Int,
                        val updated: ZonedDateTime? = null,
                        val accessed: ZonedDateTime? = null,
                        val reviewId: Int? = null,
                        val isModified: Boolean? = null)
    : Overwritable<BeerMetadata>() {

    override fun getTypeParameterClass(): Class<BeerMetadata> {
        return BeerMetadata::class.java
    }

    companion object {
        fun newUpdate(beerId: Int): BeerMetadata {
            return BeerMetadata(beerId = beerId, updated = ZonedDateTime.now())
        }

        fun newAccess(beerId: Int): BeerMetadata {
            return BeerMetadata(beerId = beerId, accessed = ZonedDateTime.now())
        }

        fun merge(v1: BeerMetadata, v2: BeerMetadata): BeerMetadata {
            val metadata = v1.copy()
            metadata.overwrite(v2)
            return metadata
        }
    }
}