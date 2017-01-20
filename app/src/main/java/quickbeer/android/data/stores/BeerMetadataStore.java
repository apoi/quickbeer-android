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
package quickbeer.android.data.stores;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import org.joda.time.DateTime;

import java.util.List;

import polanski.option.Option;
import quickbeer.android.data.columns.BeerMetadataColumns;
import quickbeer.android.data.pojos.BeerMetadata;
import quickbeer.android.data.stores.cores.BeerMetadataStoreCore;
import rx.Observable;

public class BeerMetadataStore extends StoreBase<Integer, BeerMetadata, Option<BeerMetadata>> {

    public BeerMetadataStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new BeerMetadataStoreCore(contentResolver, gson),
                BeerMetadata::beerId,
                Option::ofObj,
                Option::none);
    }

    @NonNull
    public Observable<List<Integer>> getAccessedIdsOnce() {
        BeerMetadataStoreCore core = (BeerMetadataStoreCore) getCore();

        return core.getAccessedIdsOnce(BeerMetadataColumns.ID, BeerMetadataColumns.ACCESSED);
    }

    @NonNull
    public Observable<Integer> getAccessedIdsStream(@NonNull final DateTime date) {
        return ((BeerMetadataStoreCore) getCore()).getAccessedIdsStream(date);
    }
}
