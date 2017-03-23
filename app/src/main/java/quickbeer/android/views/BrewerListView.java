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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.pojos.Header;
import quickbeer.android.features.list.BrewerListAdapter;
import quickbeer.android.utils.Countries;
import quickbeer.android.utils.StringUtils;
import quickbeer.android.viewmodels.BrewerViewModel;
import quickbeer.android.viewmodels.NetworkViewModel.ProgressStatus;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

public class BrewerListView extends FrameLayout {

    @BindView(R.id.list_view)
    RecyclerView brewersListView;

    @BindView(R.id.search_status)
    TextView searchStatusTextView;

    @Nullable
    private BrewerListAdapter brewerListAdapter;

    @NonNull
    private final PublishSubject<Integer> selectedBrewerSubject = PublishSubject.create();

    @Inject
    @Nullable
    Countries countries;

    public BrewerListView(Context context) {
        super(context);
    }

    public BrewerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        ((InjectingDrawerActivity) getContext())
                .getComponent()
                .inject(this);

        brewerListAdapter = new BrewerListAdapter(get(countries));
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

    public void setHeader(@NonNull Header header) {
        get(brewerListAdapter).setHeader(header);
    }

    public void setBrewers(@NonNull List<BrewerViewModel> brewers) {
        Timber.v("Setting " + brewers.size() + " brewers to adapter");
        get(brewerListAdapter).set(get(brewers));
    }

    public void setProgressStatus(@NonNull String progressStatus) {
        checkNotNull(searchStatusTextView);

        searchStatusTextView.setVisibility(StringUtils.hasValue(progressStatus) ? VISIBLE : GONE);
        searchStatusTextView.setText(progressStatus);
    }

}
