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

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.net.CookieManager;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.JsonIdColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.UserSettingsColumns;
import quickbeer.android.next.network.utils.LoginUtils;
import quickbeer.android.next.pojo.UserSettings;
import rx.Observable;

/**
 * Store to keep only single user settings.
 */
public class UserSettingsStore extends SingleItemContentProviderStore<UserSettings, Integer> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    public static final int DEFAULT_USER_ID = 0;
    public static final Uri LOGIN_URI = Uri.parse("UserSettingsLogin");

    private final CookieManager cookieManager;

    public UserSettingsStore(@NonNull ContentResolver contentResolver, @NonNull CookieManager cookieManager) {
        super(contentResolver);

        this.cookieManager = cookieManager;

        initUserSettings();
    }

    private void initUserSettings() {
        // Initializes settings if needed, and clears the logged in flag
        getOne().first()
                .map(userSettings -> userSettings != null
                        ? userSettings
                        : new UserSettings())
                .map(userSettings -> {
                    userSettings.setIsLogged(LoginUtils.hasLoginCookie(cookieManager));
                    return userSettings;
                })
                .doOnNext(this::put)
                .subscribe();
    }

    @NonNull
    public Observable<UserSettings> getOne() {
        return super.getOne(DEFAULT_USER_ID);
    }

    @NonNull
    public Observable<UserSettings> getStream() {
        return super.getStream(DEFAULT_USER_ID);
    }

    @Override
    public void put(@NonNull UserSettings item) {
        Log.v(TAG, "put(" + item + ")");
        super.put(item);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull UserSettings item) {
        return DEFAULT_USER_ID;
    }

    @NonNull
    @Override
    protected String getAuthority() {
        return RateBeerProvider.AUTHORITY;
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
        contentValues.put(JsonIdColumns.ID, DEFAULT_USER_ID);
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
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.UserSettings.withId(id);
    }
}
