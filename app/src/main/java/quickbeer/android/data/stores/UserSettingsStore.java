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
package quickbeer.android.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.net.CookieManager;

import io.reark.reark.utils.Log;
import polanski.option.Option;
import quickbeer.android.data.pojos.UserSettings;
import quickbeer.android.data.stores.cores.UserSettingsStoreCore;
import quickbeer.android.network.utils.LoginUtils;
import quickbeer.android.rx.RxUtils;

/**
 * Store to keep only single user settings.
 */
public class UserSettingsStore extends StoreBase<Integer, UserSettings, Option<UserSettings>> {
    private static final String TAG = UserSettingsStore.class.getSimpleName();

    public static final int DEFAULT_USER_ID = 0;

    private final CookieManager cookieManager;

    public UserSettingsStore(@NonNull final ContentResolver contentResolver,
                             @NonNull final CookieManager cookieManager,
                             @NonNull final Gson gson) {
        super(new UserSettingsStoreCore(contentResolver, gson),
              item -> DEFAULT_USER_ID,
              Option::ofObj,
              Option::none);

        this.cookieManager = cookieManager;

        initUserSettings();
    }

    private void initUserSettings() {
        // Initializes settings if needed, and clears the logged in flag
        getOnce(DEFAULT_USER_ID)
                .compose(RxUtils::pickValue)
                .map(userSettings -> {
                    userSettings.setIsLogged(LoginUtils.hasLoginCookie(cookieManager));
                    return userSettings;
                })
                .doOnNext(this::put)
                .subscribe();
    }

    @Override
    public void put(@NonNull final UserSettings item) {
        Log.v(TAG, "put(" + item + ")");
        super.put(item);
    }
}
