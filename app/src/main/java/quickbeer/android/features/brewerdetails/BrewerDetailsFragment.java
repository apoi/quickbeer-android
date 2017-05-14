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
import android.support.v4.app.Fragment;
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
import quickbeer.android.viewmodels.BrewerViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BrewerDetailsFragment extends BindingBaseFragment {

    @BindView(R.id.brewer_details_view)
    BrewerDetailsView detailsView;

    @Inject
    @Nullable
    BrewerViewModel brewerViewModel;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    private int brewerId;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel()
                    .getBrewer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(detailsView::setBrewer, Timber::e));
        }
    };

    public static Fragment newInstance(int brewerId) {
        BrewerDetailsFragment fragment = new BrewerDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("brewerId", brewerId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState != null
                ? savedInstanceState
                : getArguments();

        ofObj(bundle)
                .map(state -> state.getInt("brewerId"))
                .ifSome(value -> brewerId = value)
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.brewer_details_fragment_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel().setBrewerId(brewerId);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("brewerId", brewerId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @NonNull
    @Override
    protected BrewerViewModel viewModel() {
        return get(brewerViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }

}
