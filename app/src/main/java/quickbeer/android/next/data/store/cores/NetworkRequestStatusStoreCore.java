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
package quickbeer.android.next.data.store.cores;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.next.data.schematicprovider.JsonIdColumns;
import quickbeer.android.next.data.schematicprovider.NetworkRequestStatusColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider.NetworkRequestStatuses;
import rx.Observable;

import static io.reark.reark.utils.Preconditions.checkNotNull;

public class NetworkRequestStatusStoreCore extends StoreCoreBase<Integer, NetworkRequestStatus> {

    public NetworkRequestStatusStoreCore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(contentResolver, gson);
    }

    @NonNull
    @Override
    protected Observable<List<ContentProviderOperation>> groupOperations(@NonNull final Observable<ContentProviderOperation> source) {
        // NetworkRequestStatus updates should not be grouped to ensure fast processing.
        return source.map(Collections::singletonList);
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return NetworkRequestStatuses.NETWORK_REQUEST_STATUSES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { NetworkRequestStatusColumns.ID, NetworkRequestStatusColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(@NonNull final NetworkRequestStatus item) {
        checkNotNull(item);

        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getUri().hashCode());
        contentValues.put(JsonIdColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected NetworkRequestStatus read(@NonNull final Cursor cursor) {
        checkNotNull(cursor);

        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return getGson().fromJson(json, NetworkRequestStatus.class);
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull final Integer id) {
        checkNotNull(id);

        return NetworkRequestStatuses.withId(id);
    }

    @NonNull
    @Override
    protected Integer getIdForUri(@NonNull final Uri uri) {
        checkNotNull(uri);

        return (int) NetworkRequestStatuses.fromUri(uri);
    }
}
