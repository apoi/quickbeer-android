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
package quickbeer.android.providers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.reark.pojo.NetworkRequestStatus;
import polanski.option.Option;
import quickbeer.android.Constants;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkService;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.fetchers.LoginFetcher;
import quickbeer.android.rx.RxUtils;
import rx.Observable;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static quickbeer.android.data.stores.NetworkRequestStatusStore.requestIdForUri;

public class UserProvider {

    @NonNull
    private final Context context;

    @NonNull
    private final UserStore userStore;

    @NonNull
    private final NetworkRequestStatusStore requestStatusStore;

    @Inject
    public UserProvider(@NonNull final Context context,
                 @NonNull final UserStore userStore,
                 @NonNull final NetworkRequestStatusStore requestStatusStore) {
        this.context = get(context);
        this.userStore = get(userStore);
        this.requestStatusStore = get(requestStatusStore);
    }

    public Observable<Option<User>> getUser() {
        return userStore.getOnce(Constants.DEFAULT_USER_ID);
    }

    public Observable<Boolean> login(@NonNull final String username,
                                     @NonNull final String password) {
        Timber.v("login");

        Intent intent = new Intent(context, NetworkService.class);
        intent.putExtra("serviceUriString", RateBeerService.LOGIN.toString());
        intent.putExtra("username", get(username));
        intent.putExtra("password", get(password));
        context.startService(intent);

        // Observe the success of the network request. Completion means we logged in
        // successfully, while on error the login wasn't successful.
        return requestStatusStore
                .getOnceAndStream(requestIdForUri(LoginFetcher.getUniqueUri()))
                .compose(RxUtils::pickValue)
                .filter(requestStatus -> requestStatus.isCompleted() || requestStatus.isError())
                .first()
                .map(NetworkRequestStatus::isCompleted);
    }

}
