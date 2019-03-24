/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.network.fetchers.impl;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.pojo.NetworkRequestStatus;
import org.jetbrains.annotations.NotNull;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.fetchers.CheckingFetcher;
import quickbeer.android.network.utils.LoginUtils;
import quickbeer.android.utils.StringUtils;
import timber.log.Timber;

import java.util.Arrays;
import java.util.List;

import static io.reark.reark.utils.Preconditions.get;

public class LoginFetcher extends CheckingFetcher {

    public static final String NAME = "__login";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

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
        super(networkRequestStatus, NAME);

        this.networkApi = get(networkApi);
        this.cookieJar = get(cookieJar);
        this.userStore = get(userStore);
    }

    @Override
    public @NotNull List<String> required() {
        return Arrays.asList(USERNAME, PASSWORD);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        if (!validateParams(intent)) return;

        String uri = getUniqueUri();
        int requestId = uri.hashCode();

        addListener(requestId, listenerId);

        if (isOngoingRequest(requestId)) {
            Timber.d("Found an ongoing request for login");
            return;
        }

        String username = intent.getStringExtra(USERNAME);
        String password = intent.getStringExtra(PASSWORD);

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
    public static String getUniqueUri() {
        return User.class + "/" + Constants.DEFAULT_USER_ID;
    }
}
