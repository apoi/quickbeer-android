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
package quickbeer.android.next.data.store;

import com.google.gson.Gson;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import polanski.option.Option;
import quickbeer.android.next.data.store.cores.ReviewListStoreCore;
import quickbeer.android.next.pojo.ItemList;

/**
 * Class storing lists of reviews related to a specific beer id.
 */
public class ReviewListStore extends StoreBase<Integer, ItemList<Integer>, Option<ItemList<Integer>>> {

    public ReviewListStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new ReviewListStoreCore(contentResolver, gson),
              ItemList::getKey,
              Option::ofObj,
              Option::none);
    }
}
