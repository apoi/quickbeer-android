package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.data.schematicprovider.JsonIdColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.UserSettingsColumns;

public class NetworkRequestStatusStore extends StoreBase<NetworkRequestStatus, Integer> {
    private static final String TAG = NetworkRequestStatusStore.class.getSimpleName();

    public NetworkRequestStatusStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);

        QuickBeer.getInstance().getGraph().inject(this);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network Request Status cannot be null.");
        return item.getUri().hashCode();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.NetworkRequestStatuses.NETWORK_REQUEST_STATUSES;
    }

    @Override
    public void put(@NonNull NetworkRequestStatus item) {
        Preconditions.checkNotNull(item, "Network Request Status cannot be null.");

        Log.v(TAG, "insertOrUpdate(" + item.getStatus() + ", " + item.getUri() + ")");
        super.put(item);
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
    protected ContentValues readRaw(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.JSON, json);
        return contentValues;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.NetworkRequestStatuses.withId(id);
    }

    @Override
    protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
        return v1.getAsString(JsonIdColumns.JSON).equals(v2.getAsString(JsonIdColumns.JSON));
    }
}
