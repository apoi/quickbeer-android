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

import com.franmontiel.persistentcookiejar.ClearableCookieJar;

import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.utils.StringUtils;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class LoginFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final ClearableCookieJar cookieJar;

    @NonNull
    private final UserStore userStore;

    public LoginFetcher(@NonNull final NetworkApi networkApi,
                        @NonNull final ClearableCookieJar cookieJar,
                        @NonNull final Action1<NetworkRequestStatus> networkRequestStatus,
                        @NonNull final UserStore userStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.cookieJar = get(cookieJar);
        this.userStore = get(userStore);
    }

    @Override
    public void fetch(@NonNull final Intent intent) {
        final String uri = getUniqueUri();
        final int id = uri.hashCode();

        if (isOngoingRequest(id)) {
            Timber.d("Found an ongoing request for login");
            return;
        }

        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        if (!StringUtils.hasValue(username) || !StringUtils.hasValue(password)) {
            Timber.d("Missing username or password");
            return;
        }

        cookieJar.clear();

        Timber.d("Login with user " + username);

        Subscription subscription = networkApi
                .login(username, password)
                .subscribeOn(Schedulers.computation())
                .map(user -> User.builder()
                        .username(username)
                        .password(password)
                        .build())
                .doOnSubscribe(() -> startRequest(uri))
                .doOnCompleted(() -> completeRequest(uri))
                .doOnError(doOnError(uri))
                .doOnNext(user -> Timber.d("Updating login status to " + user.isLogged()))
                .subscribe(userStore::put,
                           err -> Timber.e(err, "Error fetching user " + username));

        addRequest(id, subscription);
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.LOGIN;
    }

    @NonNull
    public static String getUniqueUri() {
        return User.class + "/" + Constants.DEFAULT_USER_ID;
    }
}
