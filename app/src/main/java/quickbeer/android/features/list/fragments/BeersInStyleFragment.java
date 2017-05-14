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
package quickbeer.android.features.list.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.injections.IdModule;
import quickbeer.android.rx.RxUtils;
import quickbeer.android.viewmodels.BeersInStyleViewModel;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BeersInStyleFragment extends BeerListFragment {

    @BindView(R.id.style_name)
    TextView styleName;

    @BindView(R.id.style_description)
    TextView styleDescription;

    @Nullable
    @Inject
    BeersInStyleViewModel beersInStyleViewModel;

    private int styleId;

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            BeersInStyleFragment.super.dataBinder().bind(subscription);

            viewModel().styleName()
                    .toObservable()
                    .compose(RxUtils::pickValue)
                    .subscribe(styleName::setText, Timber::e);

            viewModel().styleDescription()
                    .toObservable()
                    .compose(RxUtils::pickValue)
                    .subscribe(styleDescription::setText, Timber::e);
        }

        @Override
        public void unbind() {
            BeersInStyleFragment.super.dataBinder().unbind();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = savedInstanceState != null
                ? savedInstanceState
                : getArguments();

        ofObj(bundle)
                .map(state -> state.getInt("styleId", -1))
                .ifSome(value -> styleId = value)
                .ifNone(() -> Timber.w("Expected state for initializing!"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("styleId", styleId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void inject() {
        super.inject();

        getComponent()
                .plusId(new IdModule(styleId))
                .inject(this);
    }

    @Override
    protected int getLayout() {
        return R.layout.beers_in_style_fragment;
    }

    @NonNull
    @Override
    protected BeersInStyleViewModel viewModel() {
        return get(beersInStyleViewModel);
    }

    @Override
    protected void onQuery(@NonNull String query) {
        // No action, new search replaces old results.
    }

    @Override
    @NonNull
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
