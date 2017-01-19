/**
 * This file is part of QuickBrewer.
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

import polanski.option.Option;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.cores.BrewerListStoreCore;

/**
 * Class storing brewer lists related to a string key, such as a search.
 */
public class BrewerListStore extends StoreBase<String, ItemList<String>, Option<ItemList<String>>> {

    public BrewerListStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new BrewerListStoreCore(contentResolver, gson),
              ItemList::getKey,
              Option::ofObj,
              Option::none);
    }
}
