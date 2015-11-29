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

        initUserSettings();
    }

    private void initUserSettings() {
        getOne(DataLayer.DEFAULT_USER_ID)
                .first()
                .filter(userSettings -> userSettings == null)
                .subscribe(userSettings -> {
                    put(new UserSettings());
                });
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
        return new Gson().fromJson(json, UserSettings.class);
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
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.UserSettings.withId(id);
    }

    @Override
    protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
        return v1.getAsString(JsonIdColumns.JSON).equals(v2.getAsString(JsonIdColumns.JSON));
    }
}
