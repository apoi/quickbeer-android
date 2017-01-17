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
package quickbeer.android.data.providers;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

import quickbeer.android.data.columns.BeerColumns;
import quickbeer.android.data.columns.BeerListColumns;
import quickbeer.android.data.columns.NetworkRequestStatusColumns;
import quickbeer.android.data.columns.ReviewColumns;
import quickbeer.android.data.columns.ReviewListColumns;
import quickbeer.android.data.columns.UserSettingsColumns;

@Database(version = RateBeerDatabase.VERSION)
final class RateBeerDatabase {

    static final int VERSION = 1;

    @Table(NetworkRequestStatusColumns.class) static final String NETWORK_REQUEST_STATUSES = "networkRequestStatuses";
    @Table(UserSettingsColumns.class) static final String USER_SETTINGS = "userSettings";
    @Table(BeerColumns.class) static final String BEERS = "beers";
    @Table(BeerListColumns.class) static final String BEER_LISTS = "beerLists";
    @Table(ReviewColumns.class) static final String REVIEWS = "reviews";
    @Table(ReviewListColumns.class) static final String REVIEW_LISTS = "reviewsLists";
    @Table(BeerColumns.class) static final String BREWERS = "brewers";
    @Table(BeerListColumns.class) static final String BREWER_LISTS = "brewerLists";
}
