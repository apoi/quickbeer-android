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
package quickbeer.android.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import quickbeer.android.R;
import quickbeer.android.data.pojos.Header;
import quickbeer.android.features.list.BeerListAdapter;
import quickbeer.android.utils.StringUtils;
import quickbeer.android.viewmodels.BeerViewModel;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BeerListView extends FrameLayout {

    @Nullable
    private BeerListAdapter beerListAdapter;

    @Nullable
    private RecyclerView beersListView;

    @Nullable
    private TextView searchStatusTextView;

    @NonNull
    private final PublishSubject<Integer> selectedBeerSubject = PublishSubject.create();

    public BeerListView(Context context) {
        super(context);
    }

    public BeerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beersListView = (RecyclerView) get(findViewById(R.id.list_view));
        searchStatusTextView = (TextView) findViewById(R.id.search_status);

        beerListAdapter = new BeerListAdapter();
        beerListAdapter.setOnClickListener(v -> {
            final int itemPosition = beersListView.getChildAdapterPosition(v);
            final int beerId = beerListAdapter.getBeerViewModel(itemPosition).getBeerId();
            selectedBeerSubject.onNext(beerId);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setRecycleChildrenOnDetach(true);

        beersListView.setAdapter(beerListAdapter);
        beersListView.setLayoutManager(layoutManager);
    }

    @NonNull
    public Observable<Integer> selectedBeerStream() {
        return selectedBeerSubject.asObservable();
    }

    public void setHeader(@NonNull Header header) {
        get(beerListAdapter).setHeader(header);
    }

    public void setBeers(@NonNull List<BeerViewModel> beers) {
        Timber.v("Setting " + beers.size() + " beers to adapter");
        get(beerListAdapter).set(get(beers));
    }

    public void setProgressStatus(@NonNull String progressStatus) {
        checkNotNull(searchStatusTextView);

        searchStatusTextView.setVisibility(StringUtils.hasValue(progressStatus) ? VISIBLE : GONE);
        searchStatusTextView.setText(progressStatus);
    }

}
