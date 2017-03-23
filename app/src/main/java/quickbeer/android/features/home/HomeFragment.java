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
package quickbeer.android.features.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.Unbinder;
import polanski.option.AtomicOption;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events.Screen;
import quickbeer.android.core.fragment.BindingBaseFragment;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.features.barcodescanner.BarcodeScanActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.viewmodels.SearchViewViewModel;
import quickbeer.android.viewmodels.SearchViewViewModel.Mode;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class HomeFragment extends BindingBaseFragment {

    private static final int CODE_CAPTURE_BARCODE = 9876;

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.barcode_scan_fab)
    FloatingActionButton barcodeScanButton;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    SearchViewViewModel searchViewViewModel;

    @Nullable
    @Inject
    Analytics analytics;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
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
        return inflater.inflate(R.layout.home_fragment_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewPager.setAdapter(new HomeViewAdapter(getChildFragmentManager(), getContext()));
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                Screen screen = (position == 0)
                        ? Screen.HOME_BEERS
                        : Screen.HOME_BREWERS;
                get(analytics).createEvent(screen);
            }
        });

        barcodeScanButton.setOnClickListener(__ -> {
            Intent intent = new Intent(getActivity(), BarcodeScanActivity.class);
            startActivityForResult(intent, CODE_CAPTURE_BARCODE);
        });

        viewModel().setMode(Mode.SEARCH,
                get(resourceProvider).getString(R.string.search_box_hint_search_beers));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CODE_CAPTURE_BARCODE) {
            super.onActivityResult(requestCode, resultCode, data);
        }

        if (resultCode != CommonStatusCodes.SUCCESS) {
            Timber.e("Failure capturing barcode: %s", CommonStatusCodes.getStatusCodeString(resultCode));
            return;
        }

        if (data == null) {
            Timber.e("No barcode captured, intent data is null");
            return;
        }

        Barcode barcode = data.getParcelableExtra(BarcodeScanActivity.BARCODE_RESULT);
        Timber.d("Barcode found: " + barcode.displayValue);

        Bundle bundle = new Bundle();
        bundle.putString("barcode", barcode.displayValue);

        get(navigationProvider).addPage(Page.BARCODE_SEARCH, bundle);
    }
}

