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

import quickbeer.android.R;
import quickbeer.android.adapters.SimpleListAdapter;
import quickbeer.android.utils.SimpleListSource;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class SimpleListView extends FrameLayout {

    @Nullable
    private SimpleListAdapter simpleListAdapter;

    @NonNull
    private final PublishSubject<Integer> selectedId = PublishSubject.create();

    public SimpleListView(Context context) {
        super(context);
    }

    public SimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @NonNull
    public Observable<Integer> selectionStream() {
        return selectedId.asObservable();
    }

    public void setListSource(@NonNull final SimpleListSource source) {
        RecyclerView simpleListView = (RecyclerView) findViewById(R.id.simple_list_view);

        simpleListAdapter = new SimpleListAdapter(get(source).getList());
        simpleListAdapter.setOnClickListener(v -> {
            int index = simpleListView.getChildAdapterPosition(v);
            simpleListAdapter.getItemAt(index)
                    .ifSome(item -> selectedId.onNext(item.getId()))
                    .ifNone(() -> Timber.e("No item at index %s!", index));
        });

        simpleListView.setHasFixedSize(true);
        simpleListView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleListView.setAdapter(simpleListAdapter);
    }

    public void setFilter(@NonNull final String filter) {
        get(simpleListAdapter).filterList(get(filter));
    }
}
