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
package quickbeer.android.features.main.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.features.main.MainViewAdapter;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.viewmodels.SearchViewViewModel;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class MainFragment extends BindingBaseFragment {

    @Nullable
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @Nullable
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            subscription.add(viewModel().getQueryStream()
                    .subscribe(query -> get(navigationProvider).triggerSearch(query),
                            Timber::e));
        }
    };

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        get(viewPager).setAdapter(new MainViewAdapter(getChildFragmentManager(), getContext()));
        get(tabLayout).setupWithViewPager(viewPager);

        viewModel().setSearchHint(get(resourceProvider)
                .getString(R.string.search_box_hint_search_beers));
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @NonNull
    @Override
    protected SearchViewViewModel viewModel() {
        return get(searchViewViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
