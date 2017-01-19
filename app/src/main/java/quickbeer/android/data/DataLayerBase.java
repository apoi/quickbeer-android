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
package quickbeer.android.data;

import android.support.annotation.NonNull;

import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BrewerListStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;

import static io.reark.reark.utils.Preconditions.get;

public class DataLayerBase {
    protected final NetworkRequestStatusStore networkRequestStatusStore;

    protected final BeerStore beerStore;
    protected final BeerListStore beerListStore;
    protected final BeerMetadataStore beerMetadataStore;

    protected final ReviewStore reviewStore;
    protected final ReviewListStore reviewListStore;

    protected final BrewerStore brewerStore;
    protected final BrewerListStore brewerListStore;
    protected final BrewerMetadataStore brewerMetadataStore;

    protected DataLayerBase(@NonNull final NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull final BeerStore beerStore,
                            @NonNull final BeerListStore beerListStore,
                            @NonNull final BeerMetadataStore beerMetadataStore,
                            @NonNull final ReviewStore reviewStore,
                            @NonNull final ReviewListStore reviewListStore,
                            @NonNull final BrewerStore brewerStore,
                            @NonNull final BrewerListStore brewerListStore,
                            @NonNull final BrewerMetadataStore brewerMetadataStore) {
        this.networkRequestStatusStore = get(networkRequestStatusStore);
        this.beerStore = get(beerStore);
        this.beerListStore = get(beerListStore);
        this.beerMetadataStore = get(beerMetadataStore);
        this.reviewStore = get(reviewStore);
        this.reviewListStore = get(reviewListStore);
        this.brewerStore = get(brewerStore);
        this.brewerListStore = get(brewerListStore);
        this.brewerMetadataStore = get(brewerMetadataStore);
    }
}
