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

import android.net.Uri;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import quickbeer.android.BuildConfig;
import quickbeer.android.data.columns.BeerColumns;
import quickbeer.android.data.columns.BeerListColumns;
import quickbeer.android.data.columns.BeerMetadataColumns;
import quickbeer.android.data.columns.BrewerColumns;
import quickbeer.android.data.columns.BrewerListColumns;
import quickbeer.android.data.columns.BrewerMetadataColumns;
import quickbeer.android.data.columns.NetworkRequestStatusColumns;
import quickbeer.android.data.columns.ReviewColumns;
import quickbeer.android.data.columns.ReviewListColumns;
import quickbeer.android.data.columns.UserColumns;

@ContentProvider(authority = RateBeerProvider.AUTHORITY, database = RateBeerDatabase.class)
public final class RateBeerProvider {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".providers.RateBeerProvider";
    private static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
    private static final String BASE_TYPE = "vnd.android.cursor.item/";

    private static Uri buildUri(@NonNull String... paths) {
        Uri.Builder builder = AUTHORITY_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = RateBeerDatabase.USERS)
    public static final class Users {
        @ContentUri(
                path = RateBeerDatabase.USERS,
                type = BASE_TYPE + RateBeerDatabase.USERS,
                defaultSort = UserColumns.ID + " ASC")
        public static final Uri USERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.USERS);

        @InexactContentUri(
                path = RateBeerDatabase.USERS + "/*",
                name = "USERS_ID",
                type = BASE_TYPE + RateBeerDatabase.USERS,
                whereColumn = UserColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.USERS, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REQUEST_STATUSES)
    public static final class NetworkRequestStatuses {
        @ContentUri(
                path = RateBeerDatabase.REQUEST_STATUSES,
                type = BASE_TYPE + RateBeerDatabase.REQUEST_STATUSES,
                defaultSort = NetworkRequestStatusColumns.ID + " ASC")
        public static final Uri NETWORK_REQUEST_STATUSES = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REQUEST_STATUSES);

        @InexactContentUri(
                path = RateBeerDatabase.REQUEST_STATUSES + "/*",
                name = "REQUEST_STATUSES_ID",
                type = BASE_TYPE + RateBeerDatabase.REQUEST_STATUSES,
                whereColumn = NetworkRequestStatusColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.REQUEST_STATUSES, String.valueOf(id));
        }

        public static long fromUri(Uri uri) {
            return Long.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEERS)
    public static final class Beers {
        @ContentUri(
                path = RateBeerDatabase.BEERS,
                type = BASE_TYPE + RateBeerDatabase.BEERS,
                defaultSort = BeerColumns.ID + " ASC")
        public static final Uri BEERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEERS);

        @InexactContentUri(
                path = RateBeerDatabase.BEERS + "/*",
                name = "BEERS_ID",
                type = BASE_TYPE + RateBeerDatabase.BEERS,
                whereColumn = BeerColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.BEERS, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEER_LISTS)
    public static final class BeerLists {
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

        public static String fromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEER_METADATA)
    public static final class BeerMetadata {
        @ContentUri(
                path = RateBeerDatabase.BEER_METADATA,
                type = BASE_TYPE + RateBeerDatabase.BEER_METADATA,
                defaultSort = BeerMetadataColumns.ID + " ASC")
        public static final Uri BEER_METADATA = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEER_METADATA);

        @InexactContentUri(
                path = RateBeerDatabase.BEER_METADATA + "/*",
                name = "BEER_METADATA_ID",
                type = BASE_TYPE + RateBeerDatabase.BEER_METADATA,
                whereColumn = BeerMetadataColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.BEER_METADATA, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWERS)
    public static final class Brewers {
        @ContentUri(
                path = RateBeerDatabase.BREWERS,
                type = BASE_TYPE + RateBeerDatabase.BREWERS,
                defaultSort = BrewerColumns.ID + " ASC")
        public static final Uri BREWERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWERS);

        @InexactContentUri(
                path = RateBeerDatabase.BREWERS + "/*",
                name = "BREWERS_ID",
                type = BASE_TYPE + RateBeerDatabase.BREWERS,
                whereColumn = BrewerColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.BREWERS, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWER_LISTS)
    public static final class BrewerLists {
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

        public static String fromUri(Uri uri) {
            return uri.getLastPathSegment();
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWER_METADATA)
    public static final class BrewerMetadata {
        @ContentUri(
                path = RateBeerDatabase.BREWER_METADATA,
                type = BASE_TYPE + RateBeerDatabase.BREWER_METADATA,
                defaultSort = BrewerMetadataColumns.ID + " ASC")
        public static final Uri BREWER_METADATA = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWER_METADATA);

        @InexactContentUri(
                path = RateBeerDatabase.BREWER_METADATA + "/*",
                name = "BREWER_METADATA_ID",
                type = BASE_TYPE + RateBeerDatabase.BREWER_METADATA,
                whereColumn = BrewerMetadataColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.BREWER_METADATA, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEWS)
    public static final class Reviews {
        @ContentUri(
                path = RateBeerDatabase.REVIEWS,
                type = BASE_TYPE + RateBeerDatabase.REVIEWS,
                defaultSort = ReviewColumns.ID + " ASC")
        public static final Uri REVIEWS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REVIEWS);

        @InexactContentUri(
                path = RateBeerDatabase.REVIEWS + "/*",
                name = "REVIEWS_ID",
                type = BASE_TYPE + RateBeerDatabase.REVIEWS,
                whereColumn = ReviewColumns.ID,
                pathSegment = 1)
        public static Uri withId(int id) {
            return buildUri(RateBeerDatabase.REVIEWS, String.valueOf(id));
        }

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEW_LISTS)
    public static final class ReviewLists {
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

        public static int fromUri(Uri uri) {
            return Integer.valueOf(uri.getLastPathSegment());
        }
    }
}
