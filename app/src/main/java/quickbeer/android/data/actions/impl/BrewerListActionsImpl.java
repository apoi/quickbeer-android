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

package quickbeer.android.data.actions.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.reark.reark.data.DataStreamNotification;
import quickbeer.android.data.actions.BrewerListActions;
import quickbeer.android.data.pojos.ItemList;
import quickbeer.android.data.stores.BrewerMetadataStore;
import rx.Observable;
import timber.log.Timber;

public class BrewerListActionsImpl extends ApplicationDataLayer implements BrewerListActions {

    @NonNull
    private final BrewerMetadataStore brewerMetadataStore;

    @Inject
    public BrewerListActionsImpl(@NonNull Context context,
                                 @NonNull BrewerMetadataStore brewerMetadataStore) {
        super(context);

        this.brewerMetadataStore = brewerMetadataStore;
    }

    @Override
    @NonNull
    public Observable<DataStreamNotification<ItemList<String>>> getAccessed() {
        Timber.v("getAccessed");

        return brewerMetadataStore.getAccessedIdsOnce()
                .map(ids -> new ItemList<String>(null, ids, null))
                .map(DataStreamNotification::onNext);
    }
}
