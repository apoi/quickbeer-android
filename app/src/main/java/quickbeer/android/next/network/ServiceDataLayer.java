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
package quickbeer.android.next.network;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.network.fetchers.Fetcher;
import io.reark.reark.network.fetchers.UriFetcherManager;
import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.DataLayerBase;
import quickbeer.android.next.data.store.BeerListStore;
import quickbeer.android.next.data.store.BeerStore;
import quickbeer.android.next.data.store.BrewerListStore;
import quickbeer.android.next.data.store.BrewerStore;
import quickbeer.android.next.data.store.NetworkRequestStatusStore;
import quickbeer.android.next.data.store.ReviewListStore;
import quickbeer.android.next.data.store.ReviewStore;

public class ServiceDataLayer extends DataLayerBase {
    private static final String TAG = ServiceDataLayer.class.getSimpleName();

    private final UriFetcherManager fetcherManager;

    public ServiceDataLayer(@NonNull UriFetcherManager fetcherManager,
                            @NonNull NetworkRequestStatusStore networkRequestStatusStore,
                            @NonNull BeerStore beerStore,
                            @NonNull BeerListStore beerListStore,
                            @NonNull ReviewStore reviewStore,
                            @NonNull ReviewListStore reviewListStore,
                            @NonNull BrewerStore brewerStore,
                            @NonNull BrewerListStore brewerListStore) {
        super(networkRequestStatusStore,
                beerStore, beerListStore,
                reviewStore, reviewListStore,
                brewerStore, brewerListStore);

        Preconditions.checkNotNull(fetcherManager, "FetcherManager cannot be null.");

        this.fetcherManager = fetcherManager;
    }

    public void processIntent(@NonNull Intent intent) {
        Preconditions.checkNotNull(intent, "Intent cannot be null.");

        final String serviceUriString = intent.getStringExtra("serviceUriString");
        if (serviceUriString != null) {
            final Uri serviceUri = Uri.parse(serviceUriString);
            Fetcher matchingFetcher = fetcherManager.findFetcher(serviceUri);
            if (matchingFetcher != null) {
                Log.v(TAG, "Fetcher found for " + serviceUri);
                matchingFetcher.fetch(intent);
            } else {
                Log.e(TAG, "Unknown Uri " + serviceUri);
            }
        } else {
            Log.e(TAG, "No Uri defined");
        }
    }
}
