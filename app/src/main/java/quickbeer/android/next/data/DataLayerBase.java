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
package quickbeer.android.next.data;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.store.BeerListStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.BrewerListStore;
import quickbeer.android.next.data.store.BrewerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;

public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;
    protected final BeerStore beerStore;
    protected final BeerListStore beerListStore;
    protected final ReviewStore reviewStore;
    protected final ReviewListStore reviewListStore;
    protected final BrewerStore brewerStore;
    protected final BrewerListStore brewerListStore;

    protected DataLayerBase(@NonNull NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull BeerStore beerStore,
                            @NonNull BeerListStore beerListStore,
                            @NonNull ReviewStore reviewStore,
                            @NonNull ReviewListStore reviewListStore,
                            @NonNull BrewerStore brewerStore,
                            @NonNull BrewerListStore brewerListStore) {
        Preconditions.checkNotNull(networkRequestStatusStore, "Network request status store cannot be null.");
        Preconditions.checkNotNull(beerStore, "Beer store cannot be null.");
        Preconditions.checkNotNull(beerListStore, "Beer list store cannot be null.");
        Preconditions.checkNotNull(reviewStore, "Review store cannot be null.");
        Preconditions.checkNotNull(reviewListStore, "Review list store cannot be null.");
        Preconditions.checkNotNull(brewerStore, "Brewer store cannot be null.");
        Preconditions.checkNotNull(brewerListStore, "Brewer list store cannot be null.");

        this.networkRequestStatusStore = networkRequestStatusStore;
        this.beerStore = beerStore;
        this.beerListStore = beerListStore;
        this.reviewStore = reviewStore;
        this.reviewListStore = reviewListStore;
        this.brewerStore = brewerStore;
        this.brewerListStore = brewerListStore;
    }
}
