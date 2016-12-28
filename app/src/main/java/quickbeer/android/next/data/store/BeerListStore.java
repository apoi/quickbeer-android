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
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import polanski.option.Option;
import quickbeer.android.next.data.store.cores.BeerListStoreCore;
import quickbeer.android.next.network.RateBeerService;
import quickbeer.android.next.pojo.ItemList;
import rx.Observable;

import static io.reark.reark.utils.Preconditions.checkNotNull;

/**
 * Class storing beer lists related to a string key, such as a search.
 */
public class BeerListStore extends StoreBase<String, ItemList<String>, Option<ItemList<String>>> {

    public BeerListStore(@NonNull final ContentResolver contentResolver,
                         @NonNull final Gson gson) {
        super(new BeerListStoreCore(contentResolver, gson),
              ItemList::getKey,
              Option::ofObj,
              Option::none);
    }

    @NonNull
    public Observable<List<ItemList<String>>> getAllOnce(@NonNull final String id) {
        return ((BeerListStoreCore) getCore()).getAllCached(id);
    }



    // Brewer search store needs separate query identifiers for normal searches and fixed searches
    // (top50, top in country, top in style). Fixed searches come attached with a service uri
    // identifier to make sure they stand apart from the normal searches.
    public static String getQueryId(@NonNull final Uri serviceUri, @NonNull final String query) {
        checkNotNull(serviceUri);
        checkNotNull(query);

        if (serviceUri.equals(RateBeerService.SEARCH)) {
            return query;
        } else if (!query.isEmpty()) {
            return String.format("%s_%s", serviceUri, query);
        } else {
            return serviceUri.toString();
        }
    }

    public static String getQueryId(@NonNull final Uri serviceUri) {
        return getQueryId(serviceUri, "");
    }
}
