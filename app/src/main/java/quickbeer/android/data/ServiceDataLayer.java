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

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import quickbeer.android.data.DataLayerBase;
import quickbeer.android.data.stores.BeerListStore;
import quickbeer.android.data.stores.BeerMetadataStore;
import quickbeer.android.data.stores.BeerStore;
import quickbeer.android.data.stores.BrewerListStore;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.data.stores.NetworkRequestStatusStore;
import quickbeer.android.data.stores.ReviewListStore;
import quickbeer.android.data.stores.ReviewStore;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class ServiceDataLayer extends DataLayerBase {

    @NonNull
    private final UriFetcherManager fetcherManager;

    public ServiceDataLayer(@NonNull final UriFetcherManager fetcherManager,
                            @NonNull final NetworkRequestStatusStore requestStatusStore,
                            @NonNull final BeerStore beerStore,
                            @NonNull final BeerListStore beerListStore,
                            @NonNull final BeerMetadataStore beerMetadataStore,
                            @NonNull final ReviewStore reviewStore,
                            @NonNull final ReviewListStore reviewListStore,
                            @NonNull final BrewerStore brewerStore,
                            @NonNull final BrewerListStore brewerListStore,
                            @NonNull final BrewerMetadataStore brewerMetadataStore) {
        super(requestStatusStore,
                beerStore, beerListStore, beerMetadataStore,
                reviewStore, reviewListStore,
                brewerStore, brewerListStore, brewerMetadataStore);

        this.fetcherManager = get(fetcherManager);
    }

    public void processIntent(@NonNull final Intent intent) {
        checkNotNull(intent);

        final String serviceUriString = intent.getStringExtra("serviceUriString");

        if (serviceUriString == null) {
            Timber.e("No Uri defined");
            return;
        }

        final Uri serviceUri = Uri.parse(serviceUriString);
        final Fetcher<Uri> matchingFetcher = fetcherManager.findFetcher(serviceUri);

        if (matchingFetcher != null) {
            Timber.v("Fetcher found for " + serviceUri);
            matchingFetcher.fetch(intent);
        } else {
            Timber.e("Unknown Uri " + serviceUri);
        }
    }
}
