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
package quickbeer.android.network.fetchers;

import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.network.fetchers.FetcherBase;
import io.reark.reark.pojo.NetworkRequestStatus;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.BrewerMetadata;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.RateBeerService;
import quickbeer.android.network.utils.NetworkUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BrewerFetcher extends FetcherBase<Uri> {

    @NonNull
    private final NetworkApi networkApi;

    @NonNull
    private final NetworkUtils networkUtils;

    @NonNull
    private final BrewerStore brewerStore;

    @NonNull
    private final BrewerMetadataStore metadataStore;

    public BrewerFetcher(@NonNull NetworkApi networkApi,
                         @NonNull NetworkUtils networkUtils,
                         @NonNull Consumer<NetworkRequestStatus> networkRequestStatus,
                         @NonNull BrewerStore brewerStore,
                         @NonNull BrewerMetadataStore metadataStore) {
        super(networkRequestStatus);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.brewerStore = get(brewerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        checkNotNull(intent);

        if (!intent.hasExtra("id")) {
            Timber.e("Missing required fetch parameters!");
            return;
        }

        int brewerId = get(intent).getIntExtra("id", 0);
        String uri = getUniqueUri(brewerId);

        addListener(brewerId, listenerId);

        if (isOngoingRequest(brewerId)) {
            Timber.d("Found an ongoing request for brewer %s", brewerId);
            return;
        }

        Timber.d("fetchBrewer(" + brewerId + ")");

        Disposable disposable = createNetworkObservable(brewerId)
                .subscribeOn(Schedulers.io())
                .flatMap(brewerStore::put)
                .doOnSubscribe(__ -> startRequest(brewerId, uri))
                .doOnSuccess(updated -> completeRequest(brewerId, uri, updated))
                .doOnError(doOnError(brewerId, uri))
                .subscribe(__ -> metadataStore.put(BrewerMetadata.Companion.newUpdate(brewerId)),
                        error -> Timber.w(error, "Error fetching brewer %s", brewerId));

        addRequest(brewerId, disposable);
    }

    @NonNull
    private Single<Brewer> createNetworkObservable(int brewerId) {
        return networkApi.getBrewer(networkUtils.createRequestParams("b", String.valueOf(brewerId)));
    }

    @NonNull
    @Override
    public Uri getServiceUri() {
        return RateBeerService.BREWER;
    }

    @NonNull
    public static String getUniqueUri(final int id) {
        return Brewer.class + "/" + id;
    }
}
