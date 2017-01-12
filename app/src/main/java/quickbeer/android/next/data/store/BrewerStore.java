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
package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

import polanski.option.Option;
import quickbeer.android.next.data.schematicprovider.BrewerColumns;
import quickbeer.android.next.data.store.cores.BrewerStoreCore;
import quickbeer.android.next.pojo.Brewer;
import rx.Observable;

public class BrewerStore  extends StoreBase<Integer, Brewer, Option<Brewer>> {

    public BrewerStore(@NonNull final ContentResolver contentResolver, @NonNull final Gson gson) {
        super(new BrewerStoreCore(contentResolver, gson),
              Brewer::getId,
              Option::ofObj,
              Option::none);
    }

    @NonNull
    public Observable<List<Integer>> getAccessedIds() {
        return ((BrewerStoreCore) getCore()).getAccessedIds(BrewerColumns.ID, BrewerColumns.ACCESSED);
    }

    public Observable<Integer> getNewlyAccessedIds(@NonNull final Date date) {
        return ((BrewerStoreCore) getCore()).getNewlyAccessedItems(date)
                                            .map(Brewer::getId);
    }
}
