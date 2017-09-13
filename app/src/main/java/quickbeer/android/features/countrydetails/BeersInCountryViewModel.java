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
package quickbeer.android.features.countrydetails;

import android.support.annotation.NonNull;
import android.view.View;

import javax.inject.Inject;
import javax.inject.Named;

import io.reark.reark.data.DataStreamNotification;
import polanski.option.Option;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.CountryStore;
import quickbeer.android.providers.ProgressStatusProvider;
import quickbeer.android.viewmodels.BeerListViewModel;
import quickbeer.android.viewmodels.SearchViewViewModel;
import rx.Observable;
import rx.Single;
import rx.subjects.BehaviorSubject;

import static io.reark.reark.utils.Preconditions.get;

public class BeersInCountryViewModel extends BeerListViewModel {

    @NonNull
    private final DataLayer.GetBeersInCountry getBeersInCountry;

    @NonNull
    private final CountryStore countryStore;

    @NonNull
    private final Integer countryId;

    @NonNull
    private final BehaviorSubject<Boolean> detailsOpen = BehaviorSubject.create(false);

    @Inject
    BeersInCountryViewModel(@Named("id") Integer countryId,
                            @NonNull DataLayer.GetBeer getBeer,
                            @NonNull DataLayer.GetBeerSearch getBeerSearch,
                            @NonNull DataLayer.GetBeersInCountry getBeersInCountry,
                            @NonNull CountryStore countryStore,
                            @NonNull SearchViewViewModel searchViewViewModel,
                            @NonNull ProgressStatusProvider progressStatusProvider) {
        super(getBeer, getBeerSearch, searchViewViewModel, progressStatusProvider);

        this.countryId = get(countryId);
        this.getBeersInCountry = get(getBeersInCountry);
        this.countryStore = get(countryStore);
    }

    public void detailsClicked(int visibility) {
        detailsOpen.onNext(visibility != View.VISIBLE);
    }

    @NonNull
    public Single<Option<String>> countryName() {
        return countryStore.getOnce(countryId)
                .map(option -> option.map(Country::getName));
    }

    @NonNull
    public Single<Option<String>> countryDescription() {
        return countryStore.getOnce(countryId)
                .map(option -> option.map(Country::getDescription));
    }

    @NonNull
    public Observable<Boolean> detailsOpen() {
        return detailsOpen.asObservable();
    }

    @NonNull
    @Override
    protected Observable<DataStreamNotification<ItemList<String>>> dataSource() {
        return getBeersInCountry.call(String.valueOf(countryId));
    }

    @NonNull
    @Override
    protected Observable<DataStreamNotification<ItemList<String>>> reloadSource() {
        return Observable.empty();
    }
}
