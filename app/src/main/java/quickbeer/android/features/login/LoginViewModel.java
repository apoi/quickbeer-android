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
package quickbeer.android.features.login;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.core.viewmodel.SimpleViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.User;
import quickbeer.android.rx.RxUtils;
import rx.Observable;

import static io.reark.reark.utils.Preconditions.get;

public class LoginViewModel extends SimpleViewModel {

    @NonNull
    private final DataLayer.Login login;

    @NonNull
    private final DataLayer.GetUser getUser;

    @Inject
    public LoginViewModel(@NonNull DataLayer.Login login,
                          @NonNull DataLayer.GetUser getUser) {
        this.login = get(login);
        this.getUser = get(getUser);
    }

    public Observable<User> login(@NonNull String username, @NonNull String password) {
        return login.call(username, password)
                .filter(DataStreamNotification::isOnNext)
                .map(DataStreamNotification::getValue);
    }

    public Observable<User> getUser() {
        return getUser.call()
                .compose(RxUtils::pickValue);
    }

}
