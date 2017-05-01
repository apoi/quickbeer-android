/**
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
package quickbeer.android.features.profile;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.User;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.utils.StringUtils;
import rx.Observable;
import rx.functions.Actions;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class ProfileLoginViewModel extends SimpleViewModel {

    @NonNull
    private final DataLayer.Login login;

    @NonNull
    private final DataLayer.GetUser getUser;

    @NonNull
    private final DataLayer.GetLoginStatus getLoginStatus;

    @NonNull
    private final DataLayer.FetchTickedBeers fetchTickedBeers;

    @NonNull
    private final PublishSubject<Pair<String, String>> loginSubject = PublishSubject.create();

    @NonNull
    private final PublishSubject<String> errorSubject = PublishSubject.create();

    @NonNull
    private final ResourceProvider resourceProvider;

    @Inject
    public ProfileLoginViewModel(@NonNull DataLayer.Login login,
                                 @NonNull DataLayer.GetLoginStatus getLoginStatus,
                                 @NonNull DataLayer.GetUser getUser,
                                 @NonNull DataLayer.FetchTickedBeers fetchTickedBeers,
                                 @NonNull ResourceProvider resourceProvider) {
        this.login = get(login);
        this.getLoginStatus = get(getLoginStatus);
        this.getUser = get(getUser);
        this.fetchTickedBeers = get(fetchTickedBeers);
        this.resourceProvider = get(resourceProvider);
    }

    @Override
    protected void bind(@NonNull CompositeSubscription subscription) {
        loginSubject.asObservable()
                .flatMap(user -> login.call(user.first, user.second))
                .doOnNext(this::handleErrors)
                .subscribe(Actions.empty(), Timber::e);
    }

    public void login(@NonNull String username, @NonNull String password) {
        loginSubject.onNext(Pair.create(username, password));
    }

    public void fetchTicks(@NonNull Integer userId) {
        fetchTickedBeers.call(String.valueOf(userId));
    }

    @NonNull
    public Observable<Boolean> isLoggedIn() {
        return getUser.call()
                .map(Option::isSome);
    }

    @NonNull
    public Observable<Boolean> isLoginInProgress() {
        return getLoginStatus.call()
                .map(DataStreamNotification::isOngoing);
    }

    private void handleErrors(@NonNull DataStreamNotification<User> notification) {
        if (notification.isCompletedWithError()) {
            errorSubject.onNext(notification.getError());
        }
    }

    @NonNull
    public Observable<String> errorStream() {
        return errorSubject.asObservable()
                .map(this::toReadableError);
    }

    @NonNull
    public Observable<User> getUser() {
        return getUser.call()
                .compose(RxUtils::pickValue);
    }

    @NonNull
    private String toReadableError(@NonNull String errorMessage) {
        // We use 403 for when result is a known login failure page. For other errors,
        // just show a generic error message.
        return resourceProvider.getString(StringUtils.value(errorMessage).contains("403")
                ? R.string.login_failed
                : R.string.login_error);
    }

}
