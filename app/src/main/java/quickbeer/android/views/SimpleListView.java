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
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import quickbeer.android.R;
import quickbeer.android.adapters.SimpleListAdapter;
import quickbeer.android.utils.SimpleListSource;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;

public class SimpleListView extends FrameLayout {

    @Nullable
    private SimpleListAdapter simpleListAdapter;

    @Nullable
    private RecyclerView simpleListView;

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
        return selectedId.hide();
    }

    public void setListSource(@NonNull SimpleListSource<?> source) {
        simpleListAdapter = new SimpleListAdapter(get(source).getList(), this::itemSelected);

        simpleListView = (RecyclerView) findViewById(R.id.simple_list_view);
        simpleListView.setHasFixedSize(true);
        simpleListView.setLayoutManager(new LinearLayoutManager(getContext()));
        simpleListView.setAdapter(simpleListAdapter);
    }

    private void itemSelected(View view) {
        int index = get(simpleListView).getChildAdapterPosition(view);
        get(simpleListAdapter).getItemAt(index)
                .ifSome(item -> selectedId.onNext(item.getId()))
                .ifNone(() -> Timber.e("No item at index %s!", index));
    }

    public void setFilter(@NonNull String filter) {
        get(simpleListAdapter).filterList(get(filter));
    }
}
