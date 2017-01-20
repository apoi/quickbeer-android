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
package quickbeer.android.data.stores.cores;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.data.stores.StoreItem;
import quickbeer.android.data.columns.BeerMetadataColumns;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.providers.RateBeerProvider;
import quickbeer.android.utils.DateUtils;
import quickbeer.android.utils.ValueUtils;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class BeerMetadataStoreCore extends StoreCoreBase<Integer, BeerMetadata> {

    public BeerMetadataStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    public Observable<List<Integer>> getAccessedIdsOnce(@NonNull final String idColumn, @NonNull final String accessColumn) {
        return Observable
                .fromCallable(() -> {
                    String[] projection = { idColumn };
                    String selection = String.format("%s > 0", accessColumn); // Has access date
                    String orderBy = String.format("%s DESC", accessColumn); // Sort by date

                    List<Integer> idList = new ArrayList<>(10);

                    final Cursor cursor = getContentResolver().query(getContentUri(), projection, selection, null, orderBy);

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                idList.add(cursor.getInt(cursor.getColumnIndex(idColumn)));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }

                    return idList;
                })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public Observable<Integer> getAccessedIdsStream(@NonNull final DateTime date) {
        return getStream()
                .map(StoreItem::item)
                .filter(item -> DateUtils.isValidDate(item.accessed()))
                .distinctUntilChanged(new Func1<BeerMetadata, Object>() {
                    // Access date as key object indicating distinction
                    private DateTime latestAccess = date;

                    @Override
                    public DateTime call(BeerMetadata item) {
                        if (item.accessed().isAfter(latestAccess)) {
                            latestAccess = item.accessed();
                        }
                        return latestAccess;
                    }
                })
                .map(BeerMetadata::beerId);
    }

    @NonNull
    @Override
    protected Uri getUriForId(@NonNull final Integer id) {
        return RateBeerProvider.BeerMetadata.withId(get(id));
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        return RateBeerProvider.BeerMetadata.fromUri(get(uri));
    }

    @NonNull
    @Override
    protected Uri getContentUri() {
        return RateBeerProvider.BeerMetadata.BEER_METADATA;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] {
                BeerMetadataColumns.ID,
                BeerMetadataColumns.UPDATED,
                BeerMetadataColumns.ACCESSED,
                BeerMetadataColumns.REVIEW_ID,
                BeerMetadataColumns.MODIFIED
        };
    }

    @NonNull
    @Override
    protected BeerMetadata read(@NonNull final Cursor cursor) {
        final int beerId = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.ID));
        final DateTime updated = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.UPDATED)));
        final DateTime accessed = DateUtils.fromDbValue(cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.ACCESSED)));
        final int reviewId = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.REVIEW_ID));
        final boolean isModified = cursor.getInt(cursor.getColumnIndex(BeerMetadataColumns.MODIFIED)) > 0;

        return BeerMetadata.builder()
                .beerId(beerId)
                .updated(updated)
                .accessed(accessed)
                .reviewId(reviewId)
                .isModified(isModified)
                .build();
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final BeerMetadata item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerMetadataColumns.ID, item.beerId());
        contentValues.put(BeerMetadataColumns.UPDATED, DateUtils.toDbValue(item.updated()));
        contentValues.put(BeerMetadataColumns.ACCESSED, DateUtils.toDbValue(item.accessed()));
        contentValues.put(BeerMetadataColumns.REVIEW_ID, item.reviewId());
        contentValues.put(BeerMetadataColumns.MODIFIED, ValueUtils.asInt(item.isModified()));

        return contentValues;
    }

    @NonNull
    @Override
    protected BeerMetadata mergeValues(@NonNull final BeerMetadata v1, @NonNull final BeerMetadata v2) {
        return BeerMetadata.merge(v1, v2);
    }
}
