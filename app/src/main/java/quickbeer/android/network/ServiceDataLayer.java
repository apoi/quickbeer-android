/*
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package quickbeer.android.network;

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