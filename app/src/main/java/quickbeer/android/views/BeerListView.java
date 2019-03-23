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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import quickbeer.android.R;
import quickbeer.android.features.list.BeerListAdapter;
import quickbeer.android.utils.StringUtils;
import quickbeer.android.viewmodels.BeerViewModel;
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setRecycleChildrenOnDetach(true);

        beersListView = (RecyclerView) get(findViewById(R.id.list_view));
        searchStatusTextView = (TextView) findViewById(R.id.search_status);

        beerListAdapter = new BeerListAdapter(this::beerSelected);
        beersListView.setAdapter(beerListAdapter);
        beersListView.setLayoutManager(layoutManager);
    }

    private void beerSelected(View view) {
        final int itemPosition = get(beersListView).getChildAdapterPosition(view);
        final int beerId = get(beerListAdapter).getBeerViewModel(itemPosition).getBeerId();
        selectedBeerSubject.onNext(beerId);
    }

    @NonNull
    public Observable<Integer> selectedBeerStream() {
        return selectedBeerSubject.hide();
    }

    public void setBeers(@NonNull List<BeerViewModel> beers) {
        Timber.v("Setting " + beers.size() + " beers to adapter");
        get(beerListAdapter).set(get(beers));
    }

    public void setProgressStatus(@NonNull String progressStatus) {
        checkNotNull(searchStatusTextView);

        boolean showProgressText = StringUtils.hasValue(progressStatus)
                && get(beerListAdapter).getItemCount() == 0;

        searchStatusTextView.setVisibility(showProgressText ? VISIBLE : GONE);
        searchStatusTextView.setText(progressStatus);
    }

}
