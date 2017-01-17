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

import org.joda.time.DateTime;

import java.util.List;

import polanski.option.Option;
import quickbeer.android.data.columns.BrewerColumns;
import quickbeer.android.data.stores.cores.BrewerStoreCore;
import quickbeer.android.pojo.Brewer;
import rx.Observable;

public class BrewerStore  extends StoreBase<Integer, Brewer, Option<Brewer>> {

    public BrewerStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new BrewerStoreCore(contentResolver, gson),
              Brewer::id,
              Option::ofObj,
              Option::none);
    }

    @NonNull
    public Observable<List<Integer>> getAccessedIds() {
        return ((BrewerStoreCore) getCore()).getAccessedIds(BrewerColumns.ID, BrewerColumns.ACCESSED);
    }

    public Observable<Integer> getNewlyAccessedIds(@NonNull final DateTime date) {
        return ((BrewerStoreCore) getCore()).getNewlyAccessedItems(date)
                                            .map(Brewer::id);
    }
}
