package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.data.schematicprovider.JsonIdColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.UserSettingsColumns;
import quickbeer.android.next.pojo.UserSettings;

/**
 * Created by ttuo on 07/01/15.
 */
public class UserSettingsStore extends SingleItemContentProviderStore<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    public UserSettingsStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
        if (!hasUserSettings()) {
            put(new UserSettings());
        }
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull UserSettings item) {
        return DataLayer.DEFAULT_USER_ID;
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.UserSettings.USER_SETTINGS;
    }

    private boolean hasUserSettings() {
        return query(DataLayer.DEFAULT_USER_ID) != null;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { UserSettingsColumns.ID, UserSettingsColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(UserSettings item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(JsonIdColumns.ID, DataLayer.DEFAULT_USER_ID);
        contentValues.put(JsonIdColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected UserSettings read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(JsonIdColumns.JSON));
        final UserSettings value = new Gson().fromJson(json, UserSettings.class);
        return value;
    }

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.UserSettings.withId(id);
    }
}
