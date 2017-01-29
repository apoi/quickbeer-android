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
package quickbeer.android.features.main.fragments;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.User;
import quickbeer.android.rx.RxUtils;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class TickedBeersFragment extends BeerSearchFragment {

    @Inject
    DataLayer.GetTickedBeers getTickedBeers;

    @Inject
    DataLayer.GetUsers getUsers;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            listDataBinder().bind(subscription);

            /*
            subscription.add(getUsers.call()
                    .compose(RxUtils::pickValue)
                    .filter(User::isLogged)
                    .filter(user -> !user.userId().isEmpty())
                    .map(User::userId)
                    .switchMap(id -> get(getTickedBeers.call(id)))
                    .subscribe(notification -> listViewModel().setNotification(notification),
                            Timber::e));
                            */
        }

        @Override
        public void unbind() {
            listDataBinder().unbind();
        }
    };

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    /*
    @NonNull
    @Override
    protected ViewModel viewModel() {
        return listViewModel();
    }
    */

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
