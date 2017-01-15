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
import net.simonvt.schematic.annotation.PrimaryKey;

public interface BeerColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey String ID = "id";
    @DataType(DataType.Type.TEXT) String JSON = "json";

    @DataType(DataType.Type.TEXT) String NAME = "name"; // Beer name
    @DataType(DataType.Type.INTEGER) String TICK_VALUE = "tick_value"; // Tick value
    @DataType(DataType.Type.INTEGER) String TICK_DATE = "tick_date"; // Tick date
    @DataType(DataType.Type.INTEGER) String REVIEW = "review"; // Own review id
    @DataType(DataType.Type.INTEGER) String MODIFIED = "modified"; // Flag if unsaved modifications
    @DataType(DataType.Type.INTEGER) String UPDATED = "updated"; // Date of the last data fetch
    @DataType(DataType.Type.INTEGER) String ACCESSED = "accessed"; // Date of the last access
}
