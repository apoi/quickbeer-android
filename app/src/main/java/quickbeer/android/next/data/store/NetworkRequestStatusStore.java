/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package quickbeer.android.next.data.store;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.JsonIdColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.UserSettingsColumns;
import rx.Observable;

public class NetworkRequestStatusStore extends StoreBase<NetworkRequestStatus, Integer> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(@NonNull ContentResolver contentResolver, @NonNull Gson gson) {
        // Request store is cache only since old requests won't be interesting on startup
        super(contentResolver, gson, true);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network request status cannot be null.");
        return item.getUri().hashCode();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.NetworkRequestStatuses.NETWORK_REQUEST_STATUSES;
    }

    @Override
    public void put(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network request status cannot be null.");

        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getUri() + ")");
        super.put(item);
    }

    @Override
    protected Observable<ArrayList<ContentProviderOperation>> groupOperations(Observable<ContentProviderOperation> source) {
        return source.map(operation -> new ArrayList<ContentProviderOperation>(){{ add(operation); }});
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(NetworkRequestStatus item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, item.getUri().hashCode());
        contentValues.put(JsonIdColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected NetworkRequestStatus read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        return getGson().fromJson(json, NetworkRequestStatus.class);
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.NetworkRequestStatuses.withId(id);
    }
}
