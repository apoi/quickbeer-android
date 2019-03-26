/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>
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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package quickbeer.android.data.providers

import android.net.Uri

import net.simonvt.schematic.annotation.ContentProvider
import net.simonvt.schematic.annotation.ContentUri
import net.simonvt.schematic.annotation.InexactContentUri
import net.simonvt.schematic.annotation.TableEndpoint

import quickbeer.android.BuildConfig
import quickbeer.android.data.columns.BeerColumns
import quickbeer.android.data.columns.BeerListColumns
import quickbeer.android.data.columns.BeerMetadataColumns
import quickbeer.android.data.columns.BrewerColumns
import quickbeer.android.data.columns.BrewerListColumns
import quickbeer.android.data.columns.BrewerMetadataColumns
import quickbeer.android.data.columns.NetworkRequestStatusColumns
import quickbeer.android.data.columns.ReviewColumns
import quickbeer.android.data.columns.ReviewListColumns
import quickbeer.android.data.columns.UserColumns

@ContentProvider(authority = RateBeerProvider.AUTHORITY, database = RateBeerDatabase::class)
object RateBeerProvider {
    val AUTHORITY = BuildConfig.APPLICATION_ID + ".providers.RateBeerProvider"
    private val AUTHORITY_URI = Uri.parse("content://$AUTHORITY")
    private val BASE_TYPE = "vnd.android.cursor.item/"

    private fun buildUri(vararg paths: String): Uri {
        val builder = AUTHORITY_URI.buildUpon()
        for (path in paths) {
            builder.appendPath(path)
        }
        return builder.build()
    }

    @TableEndpoint(table = RateBeerDatabase.USERS)
    object Users {
        @ContentUri(
            path = RateBeerDatabase.USERS,
            type = BASE_TYPE + RateBeerDatabase.USERS,
            defaultSort = UserColumns.ID + " ASC")
        val USERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.USERS)

        @InexactContentUri(
            path = RateBeerDatabase.USERS + "/*",
            name = "USERS_ID",
            type = BASE_TYPE + RateBeerDatabase.USERS,
            whereColumn = [UserColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.USERS, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REQUEST_STATUSES)
    object NetworkRequestStatuses {
        @ContentUri(
            path = RateBeerDatabase.REQUEST_STATUSES,
            type = BASE_TYPE + RateBeerDatabase.REQUEST_STATUSES,
            defaultSort = NetworkRequestStatusColumns.ID + " ASC")
        val NETWORK_REQUEST_STATUSES = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REQUEST_STATUSES)

        @InexactContentUri(
            path = RateBeerDatabase.REQUEST_STATUSES + "/*",
            name = "REQUEST_STATUSES_ID",
            type = BASE_TYPE + RateBeerDatabase.REQUEST_STATUSES,
            whereColumn = [NetworkRequestStatusColumns.ID],
            pathSegment = [1])
        fun withId(id: Long): Uri {
            return buildUri(RateBeerDatabase.REQUEST_STATUSES, id.toString())
        }

        fun fromUri(uri: Uri): Long {
            return java.lang.Long.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEERS)
    object Beers {
        @ContentUri(
            path = RateBeerDatabase.BEERS,
            type = BASE_TYPE + RateBeerDatabase.BEERS,
            defaultSort = BeerColumns.ID + " ASC")
        val BEERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEERS)

        @InexactContentUri(
            path = RateBeerDatabase.BEERS + "/*",
            name = "BEERS_ID",
            type = BASE_TYPE + RateBeerDatabase.BEERS,
            whereColumn = [BeerColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.BEERS, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEER_LISTS)
    object BeerLists {
        @ContentUri(
            path = RateBeerDatabase.BEER_LISTS,
            type = BASE_TYPE + RateBeerDatabase.BEER_LISTS,
            defaultSort = BeerListColumns.KEY + " ASC")
        val BEER_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEER_LISTS)

        @InexactContentUri(
            path = RateBeerDatabase.BEER_LISTS + "/*",
            name = "BEER_LISTS_ID",
            type = BASE_TYPE + RateBeerDatabase.BEER_LISTS,
            whereColumn = [BeerListColumns.KEY],
            pathSegment = [1])
        fun withKey(key: String): Uri {
            return buildUri(RateBeerDatabase.BEER_LISTS, key)
        }

        fun fromUri(uri: Uri): String? {
            return uri.lastPathSegment
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BEER_METADATA)
    object BeerMetadata {
        @ContentUri(
            path = RateBeerDatabase.BEER_METADATA,
            type = BASE_TYPE + RateBeerDatabase.BEER_METADATA,
            defaultSort = BeerMetadataColumns.ID + " ASC")
        val BEER_METADATA = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BEER_METADATA)

        @InexactContentUri(
            path = RateBeerDatabase.BEER_METADATA + "/*",
            name = "BEER_METADATA_ID",
            type = BASE_TYPE + RateBeerDatabase.BEER_METADATA,
            whereColumn = [BeerMetadataColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.BEER_METADATA, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWERS)
    object Brewers {
        @ContentUri(
            path = RateBeerDatabase.BREWERS,
            type = BASE_TYPE + RateBeerDatabase.BREWERS,
            defaultSort = BrewerColumns.ID + " ASC")
        val BREWERS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWERS)

        @InexactContentUri(
            path = RateBeerDatabase.BREWERS + "/*",
            name = "BREWERS_ID",
            type = BASE_TYPE + RateBeerDatabase.BREWERS,
            whereColumn = [BrewerColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.BREWERS, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWER_LISTS)
    object BrewerLists {
        @ContentUri(
            path = RateBeerDatabase.BREWER_LISTS,
            type = BASE_TYPE + RateBeerDatabase.BREWER_LISTS,
            defaultSort = BrewerListColumns.KEY + " ASC")
        val BREWER_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWER_LISTS)

        @InexactContentUri(
            path = RateBeerDatabase.BREWER_LISTS + "/*",
            name = "BREWER_LISTS_ID",
            type = BASE_TYPE + RateBeerDatabase.BREWER_LISTS,
            whereColumn = [BrewerListColumns.KEY],
            pathSegment = [1])
        fun withKey(key: String): Uri {
            return buildUri(RateBeerDatabase.BREWER_LISTS, key)
        }

        fun fromUri(uri: Uri): String? {
            return uri.lastPathSegment
        }
    }

    @TableEndpoint(table = RateBeerDatabase.BREWER_METADATA)
    object BrewerMetadata {
        @ContentUri(
            path = RateBeerDatabase.BREWER_METADATA,
            type = BASE_TYPE + RateBeerDatabase.BREWER_METADATA,
            defaultSort = BrewerMetadataColumns.ID + " ASC")
        val BREWER_METADATA = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.BREWER_METADATA)

        @InexactContentUri(
            path = RateBeerDatabase.BREWER_METADATA + "/*",
            name = "BREWER_METADATA_ID",
            type = BASE_TYPE + RateBeerDatabase.BREWER_METADATA,
            whereColumn = [BrewerMetadataColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.BREWER_METADATA, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEWS)
    object Reviews {
        @ContentUri(
            path = RateBeerDatabase.REVIEWS,
            type = BASE_TYPE + RateBeerDatabase.REVIEWS,
            defaultSort = ReviewColumns.ID + " ASC")
        val REVIEWS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REVIEWS)

        @InexactContentUri(
            path = RateBeerDatabase.REVIEWS + "/*",
            name = "REVIEWS_ID",
            type = BASE_TYPE + RateBeerDatabase.REVIEWS,
            whereColumn = [ReviewColumns.ID],
            pathSegment = [1])
        fun withId(id: Int): Uri {
            return buildUri(RateBeerDatabase.REVIEWS, id.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }

    @TableEndpoint(table = RateBeerDatabase.REVIEW_LISTS)
    object ReviewLists {
        @ContentUri(
            path = RateBeerDatabase.REVIEW_LISTS,
            type = BASE_TYPE + RateBeerDatabase.REVIEW_LISTS,
            defaultSort = ReviewListColumns.BEER_ID + " ASC")
        val REVIEW_LISTS = Uri.withAppendedPath(AUTHORITY_URI, RateBeerDatabase.REVIEW_LISTS)

        @InexactContentUri(
            path = RateBeerDatabase.REVIEW_LISTS + "/*",
            name = "REVIEW_LISTS_ID",
            type = BASE_TYPE + RateBeerDatabase.REVIEW_LISTS,
            whereColumn = [ReviewListColumns.BEER_ID],
            pathSegment = [1])
        fun withBeerId(beerId: Int?): Uri {
            return buildUri(RateBeerDatabase.REVIEW_LISTS, beerId.toString())
        }

        fun fromUri(uri: Uri): Int {
            return Integer.valueOf(uri.lastPathSegment!!)
        }
    }
}
