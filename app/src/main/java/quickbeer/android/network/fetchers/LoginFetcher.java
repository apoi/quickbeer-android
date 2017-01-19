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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.net.CookieManager;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import quickbeer.android.data.pojos.UserSettings;
import quickbeer.android.data.stores.UserSettingsStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.LoginUtils;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.utils.StringUtils;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static io.reark.reark.utils.Preconditions.get;

public class LoginFetcher extends FetcherBase<Uri> {
    private static final String TAG = LoginFetcher.class.getSimpleName();

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final CookieManager cookieManager;

    @NonNull
    private final UserSettingsStore userSettingsStore;

    public LoginFetcher(@NonNull final NetworkApi networkApi,
                        @NonNull final CookieManager cookieManager,
                        @NonNull final Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                        @NonNull final UserSettingsStore userSettingsStore) {
        super(updateNetworkRequestStatus);

        this.networkApi = get(networkApi);
        this.cookieManager = get(cookieManager);
        this.userSettingsStore = get(userSettingsStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final String uri = getUniqueUri();
        final int id = uri.hashCode();

        if (isOngoingRequest(id)) {
            Log.d(TAG, "Found an ongoing request for login");
            return;
        }

        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        if (!StringUtils.hasValue(username) || !StringUtils.hasValue(password)) {
            Log.d(TAG, "Missing username or password");
            return;
        }

        LoginUtils.clearLoginCredentials(cookieManager);

        Log.d(TAG, "Login with user " + username);

        Subscription subscription = networkApi
                .login(username, password)
                .subscribeOn(Schedulers.computation())
                .switchMap(response -> userSettingsStore.getOnce(UserSettingsStore.DEFAULT_USER_ID))
                .compose(RxUtils::pickValue)
                .map(userSettings -> {
                    userSettings.setUserId(LoginUtils.getUserId(cookieManager));
                    userSettings.setIsLogged(LoginUtils.hasLoginCookie(cookieManager));
                    return userSettings;
                })
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .doOnNext(userSettings -> Log.d(TAG, "Updating login status to " + userSettings.isLogged()))
                .subscribe(userSettingsStore::put,
                           Log.onError(TAG, "Error fetching user " + username));

        addRequest(id, subscription);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.LOGIN;
    }

    @NonNull
    public static String getUniqueUri() {
        return UserSettings.class + "/" + UserSettingsStore.DEFAULT_USER_ID;
    }
}
