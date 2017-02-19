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

import io.reark.reark.data.stores.cores.MemoryStoreCore;
import polanski.option.Option;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.cores.BeerListStoreCore;
import quickbeer.android.data.stores.cores.CachingStoreCore;

/**
 * Class storing beer lists related to a string key, such as a search.
 */
public class BeerListStore extends StoreBase<String, ItemList<String>, Option<ItemList<String>>> {

    public BeerListStore(@NonNull final ContentResolver contentResolver,
                         @NonNull final Gson gson) {
        super(new CachingStoreCore<>(
                new BeerListStoreCore(contentResolver, gson),
                new MemoryStoreCore<>((o1, o2) -> o2),
                ItemList::getKey),
              ItemList::getKey,
              Option::ofObj,
              Option::none);
    }
}
