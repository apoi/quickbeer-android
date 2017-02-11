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
package quickbeer.android.views.viewholders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import polanski.option.Option;
import quickbeer.android.QuickBeer;
import quickbeer.android.R;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.utils.Countries;
import quickbeer.android.utils.StringUtils;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

/**
 * View holder for all the beer details
 */
public class BeerDetailsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.beer_description)
    TextView descriptionTextView;

    @BindView(R.id.beer_style)
    TextView styleTextView;

    @BindView(R.id.brewer_name)
    TextView brewerTextView;

    @BindView(R.id.brewer_location)
    TextView locationTextView;

    @BindView(R.id.brewer_country_flag)
    ImageView countryFlag;

    @BindView(R.id.beer_rating_overall)
    TextView overallRatingTextView;

    @BindView(R.id.beer_rating_overall_column)
    View overallRatingColumn;

    @BindView(R.id.beer_rating_style)
    TextView styleRatingTextView;

    @BindView(R.id.beer_rating_style_column)
    View styleRatingColumn;

    @BindView(R.id.beer_abv)
    TextView abvTextView;

    @BindView(R.id.beer_abv_column)
    View abvColumn;

    @BindView(R.id.beer_ibu)
    TextView ibuTextView;

    @BindView(R.id.beer_ibu_column)
    View ibuColumn;

    @Nullable
    @Inject
    Countries countries;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @NonNull
    private final Context context;

    public BeerDetailsViewHolder(@NonNull View view) {
        super(view);

        ButterKnife.bind(this, view);

        context = view.getContext();

        ((QuickBeer) context.getApplicationContext())
                .graph()
                .inject(this);

        overallRatingColumn.setOnClickListener(__ ->
                showToast(R.string.description_rating_overall));

        styleRatingColumn.setOnClickListener(__ ->
                showToast(R.string.description_rating_style));

        abvColumn.setOnClickListener(__ ->
                showToast(R.string.description_abv));

        ibuColumn.setOnClickListener(__ ->
                showToast(R.string.description_ibu));
    }

    private void showToast(@StringRes int resource) {
        Toast.makeText(context, resource, Toast.LENGTH_LONG).show();
    }

    public void setBeer(@NonNull final Beer beer) {
        checkNotNull(beer);
        checkNotNull(countries);
        checkNotNull(resourceProvider);

        Option<String> na = ofObj(get(resourceProvider).getString(R.string.not_available));

        descriptionTextView.setText(StringUtils.value(beer.description(),
                resourceProvider.getString(R.string.no_description)));

        styleTextView.setText(beer.styleName());

        brewerTextView.setText(beer.brewerName());
        locationTextView.setText("Kerava, Finland");

        ofObj(beer.overallRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(() -> na)
                .ifSome(overallRatingTextView::setText);

        ofObj(beer.styleRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(() -> na)
                .ifSome(styleRatingTextView::setText);

        ofObj(beer.alcohol())
                .map(value -> String.format(Locale.ROOT, "%.1f%%", value))
                .orOption(() -> na)
                .ifSome(abvTextView::setText);

        ofObj(beer.ibu())
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(() -> na)
                .ifSome(ibuTextView::setText);

        ofObj(beer.countryId())
                .map(countries::getItem)
                .map(Country::getFlagResourceName)
                .map(resourceProvider::getDrawableIdentifier)
                .ifSome(countryFlag::setImageResource);
    }
}