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
package quickbeer.android.viewmodels;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import polanski.option.Option;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.pojos.User;
import quickbeer.android.providers.ProgressStatusProvider;
import rx.Observable;

import static io.reark.reark.utils.Preconditions.get;

public class TickedBeersViewModel extends BeerListViewModel {

    @NonNull
    private final DataLayer.GetUser getUser;

    @NonNull
    private final DataLayer.GetTickedBeers getTickedBeers;

    @NonNull
    private final DataLayer.FetchTickedBeers fetchTickedBeers;

    @Inject
    TickedBeersViewModel(@NonNull DataLayer.GetBeer getBeer,
                         @NonNull DataLayer.GetUser getUser,
                         @NonNull DataLayer.GetBeerSearch getBeerSearch,
                         @NonNull DataLayer.GetTickedBeers getTickedBeers,
                         @NonNull DataLayer.FetchTickedBeers fetchTickedBeers,
                         @NonNull SearchViewViewModel searchViewViewModel,
                         @NonNull ProgressStatusProvider progressStatusProvider) {
        super(getBeer, getBeerSearch, searchViewViewModel, progressStatusProvider);

        this.getUser = get(getUser);
        this.getTickedBeers = get(getTickedBeers);
        this.fetchTickedBeers = get(fetchTickedBeers);
    }

    @NonNull
    public Observable<Option<User>> getUser() {
        return getUser.call();
    }

    public void refreshTicks(@NonNull User user) {
        fetchTickedBeers.call(String.valueOf(get(user).id()));
    }

    @NonNull
    @Override
    protected Observable<DataStreamNotification<ItemList<String>>> dataSource() {
        return getUser.call()
                .flatMap(userOption -> userOption.match(
                        user -> get(getTickedBeers).call(String.valueOf(user.id())),
                        () -> Observable.just(DataStreamNotification.<ItemList<String>>completedWithoutValue())));
    }

    @NonNull
    @Override
    protected Observable<DataStreamNotification<ItemList<String>>> reloadSource() {
        return Observable.empty();
    }
}
