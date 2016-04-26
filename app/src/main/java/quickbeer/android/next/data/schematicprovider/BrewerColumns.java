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
package quickbeer.android.next.data.schematicprovider;

import net.simonvt.schematic.annotation.DataType;

public interface BrewerColumns extends JsonIdColumns {
    @DataType(DataType.Type.TEXT) String NAME = "name"; // Brewer name
    @DataType(DataType.Type.INTEGER) String UPDATED = "updated"; // Date of the last data fetch
    @DataType(DataType.Type.INTEGER) String ACCESSED = "accessed"; // Date of the last access
}