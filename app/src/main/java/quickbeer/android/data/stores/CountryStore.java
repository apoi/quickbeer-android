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

package quickbeer.android.data.stores;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Collection;
import java.util.Objects;

import io.reark.reark.data.stores.DefaultStore;
import ix.Ix;
import polanski.option.Option;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.stores.cores.CountryStoreCore;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.utils.SimpleListSource;
import rx.Observable;

public class CountryStore
        extends DefaultStore<Integer, Country, Option<Country>>
        implements SimpleListSource<Country.SimpleCountry> {

    public CountryStore(@NonNull ResourceProvider resourceProvider, @NonNull Gson gson) {
        super(new CountryStoreCore(resourceProvider, gson),
                Country::id,
                Option::ofObj,
                Option::none);
    }

    @Override
    public Country.SimpleCountry getItem(int id) {
        return getOnce(id)
                .toBlocking()
                .value()
                .map(Country.SimpleCountry::new)
                .orDefault(() -> null);
    }

    @Override
    public Collection<Country.SimpleCountry> getList() {
        return getOnce()
                .toObservable()
                .flatMap(Observable::from)
                .map(Country.SimpleCountry::new)
                .toList()
                .toBlocking()
                .first();
    }

    @NonNull
    public Option<Country.SimpleCountry> getStyle(@NonNull String countryName) {
        return Ix.from(getList())
                .filter(value -> Objects.equals(value.getName(), countryName))
                .map(Option::ofObj)
                .first(Option.none());
    }
}
