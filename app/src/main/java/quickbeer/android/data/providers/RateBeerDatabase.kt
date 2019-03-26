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

import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import net.simonvt.schematic.annotation.Database
import net.simonvt.schematic.annotation.OnUpgrade
import net.simonvt.schematic.annotation.Table

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
import timber.log.Timber

@Database(version = RateBeerDatabase.VERSION)
object RateBeerDatabase {

    val VERSION = 2

    @Table(NetworkRequestStatusColumns::class)
    internal val REQUEST_STATUSES = "requestStatuses"
    @Table(UserColumns::class)
    internal val USERS = "users"

    @Table(BeerColumns::class)
    internal val BEERS = "beers"
    @Table(BeerListColumns::class)
    internal val BEER_LISTS = "beerLists"
    @Table(BeerMetadataColumns::class)
    internal val BEER_METADATA = "beerMetadata"

    @Table(BrewerColumns::class)
    internal val BREWERS = "brewers"
    @Table(BrewerListColumns::class)
    internal val BREWER_LISTS = "brewerLists"
    @Table(BrewerMetadataColumns::class)
    internal val BREWER_METADATA = "brewerMetadata"

    @Table(ReviewColumns::class)
    internal val REVIEWS = "reviews"
    @Table(ReviewListColumns::class)
    internal val REVIEW_LISTS = "reviewsLists"

    private val MIGRATIONS = arrayOf(
        // Version 1 -> 2: request format was changed
        "DELETE FROM $REQUEST_STATUSES;")

    @OnUpgrade
    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for (i in oldVersion until newVersion) {
            val migration = MIGRATIONS[i - 1]
            db.beginTransaction()

            try {
                db.execSQL(migration)
                db.setTransactionSuccessful()
            } catch (e: SQLException) {
                Timber.e(e, "Error executing database migration: %s", migration)
            } finally {
                db.endTransaction()
            }
        }
    }
}
