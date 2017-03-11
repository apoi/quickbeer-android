/**
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
import quickbeer.android.features.list.BrewerListAdapter;
import quickbeer.android.viewmodels.BrewerViewModel;
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BrewerListView extends FrameLayout {

    @Nullable
    private BrewerListAdapter brewerListAdapter;

    @Nullable
    private RecyclerView brewersListView;

    @Nullable
    private TextView searchStatusTextView;

    @NonNull
    private final PublishSubject<Integer> selectedBrewerSubject = PublishSubject.create();

    public BrewerListView(Context context) {
        super(context);
    }

    public BrewerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        brewersListView = (RecyclerView) get(findViewById(R.id.list_view));
        searchStatusTextView = (TextView) findViewById(R.id.search_status);

        brewerListAdapter = new BrewerListAdapter();
        brewerListAdapter.setOnClickListener(v -> {
            final int itemPosition = brewersListView.getChildAdapterPosition(v);
            final int brewerId = brewerListAdapter.getBrewerViewModel(itemPosition).getBrewerId();
            selectedBrewerSubject.onNext(brewerId);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setRecycleChildrenOnDetach(true);

        brewersListView.setAdapter(brewerListAdapter);
        brewersListView.setLayoutManager(layoutManager);
    }

    @NonNull
    public Observable<Integer> selectedBrewerStream() {
        return selectedBrewerSubject.asObservable();
    }

    public void setHeader(@NonNull final Header header) {
        get(brewerListAdapter).setHeader(header);
    }

    public void setBrewers(@NonNull final List<BrewerViewModel> brewers) {
        Timber.v("Setting " + brewers.size() + " brewers to adapter");
        get(brewerListAdapter).set(get(brewers));
    }

    public void setProgressStatus(@NonNull final ProgressStatus progressStatus) {
        checkNotNull(searchStatusTextView);

        switch (progressStatus) {
            case VALUE:
                searchStatusTextView.setVisibility(GONE);
                break;
            case LOADING:
                searchStatusTextView.setText(R.string.search_status_loading);
                searchStatusTextView.setVisibility(VISIBLE);
                break;
            case ERROR:
                searchStatusTextView.setText(R.string.search_status_error);
                searchStatusTextView.setVisibility(VISIBLE);
                break;
            case EMPTY:
                searchStatusTextView.setText(R.string.search_no_results);
                searchStatusTextView.setVisibility(VISIBLE);
                break;
        }
    }
}
