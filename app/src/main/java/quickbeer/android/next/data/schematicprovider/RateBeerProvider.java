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

import android.net.Uri;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import io.reark.reark.utils.Preconditions;

@ContentProvider(authority = quickbeer.android.next.data.schematicprovider.RateBeerProvider.AUTHORITY, database = RateBeerDatabase.class)
public class RateBeerProvider {
    public static final String AUTHORITY = "quickbeer.android.next.data.schematicprovider.RateBeerProvider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final String BASE_TYPE = "vnd.android.cursor.item/";

    private static Uri buildUri(@NonNull String... paths) {
        Uri.Builder builder = AUTHORITY_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        Uri uri = builder.build();
        Preconditions.checkNotNull(uri, "Uri cannot be null.");
        return uri;
    }

    @TableEndpoint(table = RateBeerDatabase.USER_SETTINGS) public static class UserSettings {
        @ContentUri(
                path = RateBeerDatabase.USER_SETTINGS,
                type = BASE_TYPE + RateBeerDatabase.USER_SETTINGS,
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri USER_SETTINGS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.USER_SETTINGS);

        @InexactContentUri(
                path = RateBeerDatabase.USER_SETTINGS + "/*",
                name = "USER_SETTINGS_ID",
                type = BASE_TYPE + RateBeerDatabase.USER_SETTINGS,
                whereColumn = UserSettingsColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.USER_SETTINGS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.NETWORK_REQUEST_STATUSES) public static class NetworkRequestStatuses {
        @ContentUri(
                path = RateBeerDatabase.NETWORK_REQUEST_STATUSES,
                type = BASE_TYPE + RateBeerDatabase.NETWORK_REQUEST_STATUSES,
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri NETWORK_REQUEST_STATUSES = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.NETWORK_REQUEST_STATUSES);

        @InexactContentUri(
                path = RateBeerDatabase.NETWORK_REQUEST_STATUSES + "/*",
                name = "NETWORK_REQUEST_STATUSES_ID",
                type = BASE_TYPE + RateBeerDatabase.NETWORK_REQUEST_STATUSES,
                whereColumn = NetworkRequestStatusColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.NETWORK_REQUEST_STATUSES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEERS) public static class Beers {
        @ContentUri(
                path = RateBeerDatabase.BEERS,
                type = BASE_TYPE + RateBeerDatabase.BEERS,
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri BEERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEERS);

        @InexactContentUri(
                path = RateBeerDatabase.BEERS + "/*",
                name = "BEERS_ID",
                type = BASE_TYPE + RateBeerDatabase.BEERS,
                whereColumn = BeerColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.BEERS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEER_LISTS) public static class BeerLists {
        @ContentUri(
                path = RateBeerDatabase.BEER_LISTS,
                type = BASE_TYPE + RateBeerDatabase.BEER_LISTS,
                defaultSort = BeerListColumns.KEY + " ASC")
        public static final Uri BEER_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEER_LISTS);

        @InexactContentUri(
                path = RateBeerDatabase.BEER_LISTS + "/*",
                name = "BEER_LISTS_ID",
                type = BASE_TYPE + RateBeerDatabase.BEER_LISTS,
                whereColumn = BeerListColumns.KEY,
                pathSegment = 1)
        public static Uri withKey(String key) {
            return buildUri(RateBeerDatabase.BEER_LISTS, key);
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEWS) public static class Reviews {
        @ContentUri(
                path = RateBeerDatabase.REVIEWS,
                type = BASE_TYPE + RateBeerDatabase.REVIEWS,
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri REVIEWS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REVIEWS);

        @InexactContentUri(
                path = RateBeerDatabase.REVIEWS + "/*",
                name = "REVIEWS_ID",
                type = BASE_TYPE + RateBeerDatabase.REVIEWS,
                whereColumn = ReviewColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.REVIEWS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEW_LISTS) public static class ReviewLists {
        @ContentUri(
                path = RateBeerDatabase.REVIEW_LISTS,
                type = BASE_TYPE + RateBeerDatabase.REVIEW_LISTS,
                defaultSort = ReviewListColumns.BEER_ID + " ASC")
        public static final Uri REVIEW_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REVIEW_LISTS);

        @InexactContentUri(
                path = RateBeerDatabase.REVIEW_LISTS + "/*",
                name = "REVIEW_LISTS_ID",
                type = BASE_TYPE + RateBeerDatabase.REVIEW_LISTS,
                whereColumn = ReviewListColumns.BEER_ID,
                pathSegment = 1)
        public static Uri withBeerId(Integer beerId) {
            return buildUri(RateBeerDatabase.REVIEW_LISTS, String.valueOf(beerId));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWERS) public static class Brewers {
        @ContentUri(
                path = RateBeerDatabase.BREWERS,
                type = BASE_TYPE + RateBeerDatabase.BREWERS,
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri BREWERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWERS);

        @InexactContentUri(
                path = RateBeerDatabase.BREWERS + "/*",
                name = "BREWERS_ID",
                type = BASE_TYPE + RateBeerDatabase.BREWERS,
                whereColumn = BrewerColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.BREWERS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWER_LISTS) public static class BrewerLists {
        @ContentUri(
                path = RateBeerDatabase.BREWER_LISTS,
                type = BASE_TYPE + RateBeerDatabase.BREWER_LISTS,
                defaultSort = BrewerListColumns.KEY + " ASC")
        public static final Uri BREWER_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWER_LISTS);

        @InexactContentUri(
                path = RateBeerDatabase.BREWER_LISTS + "/*",
                name = "BREWER_LISTS_ID",
                type = BASE_TYPE + RateBeerDatabase.BREWER_LISTS,
                whereColumn = BrewerListColumns.KEY,
                pathSegment = 1)
        public static Uri withKey(String key) {
            return buildUri(RateBeerDatabase.BREWER_LISTS, key);
        }
    }
}
