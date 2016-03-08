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

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RateBeerDatabase.VERSION)
public final class RateBeerDatabase {
    public static final int VERSION = 1;

    @Table(NetworkRequestStatusColumns.class) public static final String NETWORK_REQUEST_STATUSES = "networkRequestStatuses";
    @Table(UserSettingsColumns.class) public static final String USER_SETTINGS = "userSettings";
    @Table(BeerColumns.class) public static final String BEERS = "beers";
    @Table(BeerSearchColumns.class) public static final String BEER_SEARCHES = "beerSearches";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
    @Table(ReviewListColumns.class) public static final String REVIEW_LISTS = "reviewsLists";
}
