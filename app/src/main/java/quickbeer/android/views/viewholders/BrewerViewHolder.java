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
package quickbeer.android.views.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.SimpleDataBinder;
import quickbeer.android.core.viewmodel.viewholder.BaseBindingViewHolder;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.utils.Countries;
import quickbeer.android.viewmodels.BrewerViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

public class BrewerViewHolder extends BaseBindingViewHolder<BrewerViewModel> {

    @BindView(R.id.brewer_origin)
    TextView brewerCircle;

    @BindView(R.id.brewer_name)
    TextView brewerName;

    @BindView(R.id.brewer_country)
    TextView brewerCountry;

    @NonNull
    private final Countries countries;

    @NonNull
    private final DataBinder viewDataBinder = new SimpleDataBinder() {
        @Override
        public void bind(@NonNull CompositeSubscription subscription) {
            clear();

            subscription.add(get(getViewModel())
                    .getBrewer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(BrewerViewHolder.this::setBrewer, Timber::e));
        }
    };

    public BrewerViewHolder(@NonNull View view,
                            @NonNull Countries countries,
                            @NonNull View.OnClickListener onClickListener) {
        super(view);

        this.countries = countries;

        ButterKnife.bind(this, view);
        view.setOnClickListener(onClickListener);
    }

    @Override
    @NonNull
    public DataBinder getViewDataBinder() {
        return viewDataBinder;
    }

    public void setBrewer(@NonNull Brewer brewer) {
        brewerName.setText(brewer.name());

        Option<Country> countryOption = ofObj(brewer.countryId())
                .map(countries::getItem);

        countryOption.ifSome(c -> brewerCircle.setText(c.getCode()));

        ofObj(brewer.city())
                .lift(countryOption, (city, country) -> String.format("%s, %s", city, country.getName()))
                .orOption(() -> countryOption.map(Country::getName))
                .orOption(() -> ofObj("Unknown"))
                .ifSome(origin -> brewerCountry.setText(origin));
    }

    public void clear() {
        brewerCircle.setText("");
        brewerName.setText("");
        brewerCountry.setText("");
    }
}