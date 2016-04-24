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
package quickbeer.android.next.network.fetchers;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.net.CookieManager;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.UserSettingsStore;
import quickbeer.android.next.network.NetworkApi;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.network.utils.LoginUtils;
import rx.Subscription;
import rx.functions.Action1;

public class LoginFetcher extends FetcherBase {
    private static final String TAG = LoginFetcher.class.getSimpleName();

    private final NetworkApi networkApi;
    private final CookieManager cookieManager;
    private final UserSettingsStore userSettingsStore;

    public LoginFetcher(@NonNull NetworkApi networkApi,
                        @NonNull CookieManager cookieManager,
                        @NonNull Action1<NetworkRequestStatus> updateNetworkRequestStatus,
                        @NonNull UserSettingsStore userSettingsStore) {
        super(updateNetworkRequestStatus);

        Preconditions.checkNotNull(networkApi, "Network API cannot be null.");
        Preconditions.checkNotNull(cookieManager, "Cookie manager cannot be null.");
        Preconditions.checkNotNull(userSettingsStore, "Settings store cannot be null.");

        this.networkApi = networkApi;
        this.cookieManager = cookieManager;
        this.userSettingsStore = userSettingsStore;
    }

    @Override
    public void fetch(Intent intent) {
        final String uri = UserSettingsStore.LOGIN_URI.toString();
        final int id = uri.hashCode();

        if (requestMap.containsKey(id) && !requestMap.get(id).isUnsubscribed()) {
            Log.d(TAG, "Found an ongoing request for login");
            return;
        }

        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        Subscription subscription = networkApi.login(username, password)
                .flatMap(response -> userSettingsStore.getOne())
                .map(userSettings -> {
                    userSettings.setIsLogged(LoginUtils.hasLoginCookie(cookieManager));
                    return userSettings;
                })
                .doOnNext(userSettings -> Log.d(TAG, "Updating login status to " + userSettings.isLogged()))
                .doOnNext(userSettingsStore::put)
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .subscribe();

        requestMap.put(id, subscription);
        startRequest(uri);
    }

    @Override
    public Object getServiceUri() {
        return RateBeerService.LOGIN;
    }
}
