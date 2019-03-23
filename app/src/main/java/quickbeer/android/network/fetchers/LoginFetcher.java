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
import androidx.annotation.NonNull;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.LoginUtils;
import quickbeer.android.utils.StringUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class LoginFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final ClearableCookieJar cookieJar;

    @NonNull
    private final UserStore userStore;

    public LoginFetcher(@NonNull NetworkApi networkApi,
                        @NonNull ClearableCookieJar cookieJar,
                        @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
                        @NonNull UserStore userStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.cookieJar = get(cookieJar);
        this.userStore = get(userStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("username") || !intent.hasExtra("password")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        String uri = getUniqueUri();
        int requestId = uri.hashCode();

        addListener(requestId, listenerId);

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for login");
            return;
        }

        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        if (!StringUtils.hasValue(username) || !StringUtils.hasValue(password)) {
            Timber.d("Missing username or password");
            return;
        }

        cookieJar.clear();

        Timber.d("Login with user " + username);

        Disposable disposable = networkApi
                .login(username, password)
                .subscribeOn(Schedulers.io())
                .map(__ -> LoginUtils.getUserId(cookieJar))
                .doOnSuccess(id -> id.ifNone(() -> Timber.e("No user id found in login response!")))
                .map(userId -> new User(userId.orDefault(() -> -1), username, password))
                .flatMap(userStore::put)
                .doOnSubscribe(__ -> startRequest(requestId, uri))
                .doOnSuccess(updated -> completeRequest(requestId, uri, updated))
                .doOnError(doOnError(requestId, uri))
                .subscribe(Functions.emptyConsumer(),
                        error -> Timber.e(error, "Error fetching user " + username));

        addRequest(requestId, disposable);
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
