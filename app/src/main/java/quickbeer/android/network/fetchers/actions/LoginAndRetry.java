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
package quickbeer.android.network.fetchers.actions;

import android.support.annotation.NonNull;

import quickbeer.android.Constants;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.UserStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.rx.RxUtils;
import retrofit2.HttpException;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class LoginAndRetry implements Func1<Observable<? extends Throwable>, Observable<?>> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final UserStore userStore;

    public LoginAndRetry(@NonNull NetworkApi networkApi,
                         @NonNull UserStore userStore) {
        this.networkApi = get(networkApi);
        this.userStore = get(userStore);
    }

    @Override
    public Observable<?> call(Observable<? extends Throwable> error) {
        return error.flatMap(throwable -> {
            // Attempt re-login with every HTTP error
            if (throwable instanceof HttpException) {
                Timber.d("Retrying operation after login");
                return getUserOrError(throwable)
                        .flatMapSingle(user -> networkApi.login(user.username(), user.password()))
                        .doOnError(e -> Timber.w("Retry failed, propagating login error"));
            }

            Timber.d("Not a Retrofit error, will not retry");
            return Observable.error(throwable);
        });
    }

    @NonNull
    private Observable<User> getUserOrError(@NonNull Throwable throwable) {
        return userStore.getOnce(Constants.DEFAULT_USER_ID)
                .toObservable()
                .compose(RxUtils::pickValue)
                .switchIfEmpty(Observable.error(throwable))
                .doOnError(e -> Timber.w("No login details available!"));
    }
}