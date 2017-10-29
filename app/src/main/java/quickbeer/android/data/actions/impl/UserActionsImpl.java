/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.data.actions.impl;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import io.reark.reark.data.utils.DataLayerUtils;
import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.Constants;
import quickbeer.android.data.actions.UserActions;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.LoginFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import timber.log.Timber;

public class UserActionsImpl extends ApplicationDataLayer implements UserActions {

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @NonNull
    private final UserStore userStore;

    @Inject
    public UserActionsImpl(@NonNull Context context,
                           @NonNull NetworkRequestStatusStore requestStatusStore,
                           @NonNull UserStore userStore) {
        super(context);

        this.requestStatusStore = requestStatusStore;
        this.userStore = userStore;
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<User>> login(@NonNull String username, @NonNull String password) {
        Timber.v("login(%s)", username);

        String uri = LoginFetcher.getUniqueUri();

        int listenerId = createListenerId();

        Intent intent = new Intent(getContext(), NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.LOGIN.toString());
        intent.putExtra("listenerId", listenerId);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        getContext().startService(intent);

        return getLoginStatus(listenerId);
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<User>> getLoginStatus() {
        // Note -- returns old and new statuses alike
        return getLoginStatus(null);
    }

    @NonNull
    private Observable<DataStreamNotification<User>> getLoginStatus(@Nullable Integer listenerId) {
        Timber.v("getLoginStatus()");

        String uri = LoginFetcher.getUniqueUri();

        Observable<NetworkRequestStatus> requestStatusObservable =
                requestStatusStore.getOnceAndStream(NetworkRequestStatusStore.Companion.requestIdForUri(uri))
                        .compose(RxUtils::pickValue)
                        .filter(status -> status.forListener(listenerId));

        Observable<User> userObservable =
                userStore.getOnceAndStream(Constants.DEFAULT_USER_ID)
                        .compose(RxUtils::pickValue);

        return DataLayerUtils.createDataStreamNotificationObservable(
                requestStatusObservable, userObservable);
    }

    @Override
    @NonNull
    public Observable<Option<User>> getUser() {
        return userStore.getOnceAndStream(Constants.DEFAULT_USER_ID);
    }
}
