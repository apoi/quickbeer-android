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
package quickbeer.android.features.brewerdetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events.Screen;
import quickbeer.android.core.fragment.BaseFragment;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class BrewerDetailsPagerFragment extends BaseFragment {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Nullable
    @Inject
    Analytics analytics;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    private int brewerId;

    public static Fragment newInstance(int brewerId) {
        BrewerDetailsPagerFragment fragment = new BrewerDetailsPagerFragment();
        //noinspection AccessingNonPublicFieldOfAnotherObject
        fragment.brewerId = brewerId;
        return fragment;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.brewer_details_fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager.setAdapter(new BrewerDetailsPagerAdapter(getChildFragmentManager(), brewerId));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                Screen screen = (position == 0)
                        ? Screen.BREWER_DETAILS
                        : Screen.BREWER_BEERS;
                get(analytics).createEvent(screen);
            }
        });
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

}
