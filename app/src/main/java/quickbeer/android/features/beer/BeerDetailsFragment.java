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
package quickbeer.android.features.beer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.views.BeerDetailsView;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static butterknife.ButterKnife.bind;
import static io.reark.reark.utils.Preconditions.get;

public class BeerDetailsFragment extends BindingBaseFragment {

    @BindView(R.id.beer_details_view)
    BeerDetailsView detailsView;

    @Inject
    @Nullable
    BeerDetailsViewModel beerDetailsViewModel;

    @NonNull
    private final AtomicOption<Unbinder> unbinder = new AtomicOption<>();

    @NonNull
    private final DataBinder dataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            ConnectableObservable<Beer> beerObservable = viewModel()
                    .getBeer()
                    .subscribeOn(Schedulers.computation())
                    .publish();

            subscription.add(beerObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(detailsView::setBeer, Timber::e));

            subscription.add(beerObservable.connect());

            subscription.add(viewModel()
                    .getReviews()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(detailsView::setReviews, Timber::e));
        }
    };

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder.setIfNone(bind(this, view));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int beerId = ((BeerDetailsActivity) getActivity()).getBeerId();

        viewModel().setBeerId(beerId);
    }

    @Override
    public void onDestroyView() {
        unbinder.getAndClear()
                .ifSome(Unbinder::unbind);
        super.onDestroyView();
    }

    @NonNull
    @Override
    protected BeerDetailsViewModel viewModel() {
        return get(beerDetailsViewModel);
    }

    @NonNull
    @Override
    protected DataBinder dataBinder() {
        return dataBinder;
    }
}
