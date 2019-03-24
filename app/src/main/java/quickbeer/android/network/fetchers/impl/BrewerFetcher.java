/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.network.fetchers.impl;

import android.content.Intent;
import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reark.reark.pojo.NetworkRequestStatus;
import org.jetbrains.annotations.NotNull;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.BrewerMetadata;
import quickbeer.android.data.stores.BrewerMetadataStore;
import quickbeer.android.data.stores.BrewerStore;
import quickbeer.android.network.NetworkApi;
import quickbeer.android.network.fetchers.CheckingFetcher;
import quickbeer.android.network.utils.NetworkUtils;
import timber.log.Timber;

import java.util.Collections;
import java.util.List;

import static io.reark.reark.utils.Preconditions.get;

public class BrewerFetcher extends CheckingFetcher {

    public static final String NAME = "__brewer";
    public static final String BREWER_ID = "brewerId";

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
        super(networkRequestStatus, NAME);

        this.networkApi = get(networkApi);
        this.networkUtils = get(networkUtils);
        this.brewerStore = get(brewerStore);
        this.metadataStore = get(metadataStore);
    }

    @Override
    public @NotNull
    List<String> required() {
        return Collections.singletonList(BREWER_ID);
    }

    @Override
    public void fetch(@NonNull Intent intent, int listenerId) {
        if (!validateParams(intent)) return;

        int brewerId = get(intent).getIntExtra(BREWER_ID, 0);
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
    public static String getUniqueUri(final int id) {
        return Brewer.class + "/" + id;
    }
}
