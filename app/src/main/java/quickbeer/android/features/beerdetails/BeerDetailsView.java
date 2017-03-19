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
package quickbeer.android.features.beerdetails;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.DataLayer;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.pojos.Style;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.utils.Countries;
import quickbeer.android.utils.DateUtils;
import quickbeer.android.utils.StringUtils;
import quickbeer.android.utils.Styles;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

/**
 * View holder for all the beer details
 */
public class BeerDetailsView extends NestedScrollView {

    @BindView(R.id.beer_description)
    TextView descriptionTextView;

    @BindView(R.id.beer_style)
    TextView styleTextView;

    @BindView(R.id.beer_style_row)
    View styleRow;

    @BindView(R.id.brewer_name)
    TextView brewerTextView;

    @BindView(R.id.brewer_name_row)
    View brewerNameRow;

    @BindView(R.id.brewer_location)
    TextView locationTextView;

    @BindView(R.id.brewer_location_row)
    View brewerLocationRow;

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

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.ticked_date)
    TextView tickedDate;

    @Nullable
    @Inject
    Styles styles;

    @Nullable
    @Inject
    Countries countries;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    DataLayer.TickBeer tickBeer;

    public BeerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        ((InjectingDrawerActivity) getContext())
                .getComponent()
                .inject(this);

        overallRatingColumn.setOnClickListener(__ ->
                showToast(R.string.description_rating_overall));

        styleRatingColumn.setOnClickListener(__ ->
                showToast(R.string.description_rating_style));

        abvColumn.setOnClickListener(__ ->
                showToast(R.string.description_abv));

        ibuColumn.setOnClickListener(__ ->
                showToast(R.string.description_ibu));

        ratingBar.setOnRatingBarChangeListener(BeerDetailsView::setRating);
    }

    private void showToast(@StringRes int resource) {
        Toast.makeText(getContext(), resource, Toast.LENGTH_LONG).show();
    }

    public void setBeer(@NonNull Beer beer) {
        checkNotNull(beer);
        checkNotNull(styles);
        checkNotNull(resourceProvider);

        descriptionTextView.setText(
                StringUtils.value(beer.description(),
                resourceProvider.getString(R.string.no_description)));

        brewerTextView.setText(beer.brewerName());

        ofObj(beer.styleId())
                .map(styles::getItem)
                .orOption(() -> ofObj(beer.styleName()).flatMap(styles::getStyle))
                .ifSome(style -> styleRow.setOnClickListener(__ -> navigateToStyle(style.getId())))
                .map(Style::getName)
                .orOption(this::notAvailableString)
                .ifSome(styleTextView::setText);

        ofObj(beer.overallRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(this::notAvailableString)
                .ifSome(overallRatingTextView::setText);

        ofObj(beer.styleRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(this::notAvailableString)
                .ifSome(styleRatingTextView::setText);

        ofObj(beer.alcohol())
                .map(value -> String.format(Locale.ROOT, "%.1f%%", value))
                .orOption(this::notAvailableString)
                .ifSome(abvTextView::setText);

        ofObj(beer.ibu())
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(this::notAvailableString)
                .ifSome(ibuTextView::setText);

        ofObj(beer.tickValue())
                .ifSome(ratingBar::setRating);

        ofObj(beer.tickDate())
                .map(DateUtils::formatDateTime)
                .map(date -> String.format(resourceProvider.getString(R.string.beer_tick_date), date))
                .ifSome(value -> {
                    tickedDate.setText(value);
                    tickedDate.setVisibility(View.VISIBLE);
                });
    }

    public void setBrewer(@NonNull Brewer brewer) {
        checkNotNull(countries);

        ofObj(brewer.countryId())
                .map(countries::getItem)
                .ifSome(country -> brewerLocationRow.setOnClickListener(__ -> navigateToCountry(country)))
                .map(Country::getName)
                .map(country -> String.format("%s, %s", brewer.city(), country))
                .orOption(this::notAvailableString)
                .ifSome(locationTextView::setText);
    }

    private static void setRating(@NonNull RatingBar ratingBar, float value, boolean user) {
        Timber.w("Gonna tick %s", value);
    }

    @NonNull
    private Option<String> notAvailableString() {
        return ofObj(get(resourceProvider).getString(R.string.not_available));
    }

    private void navigateToStyle(@Nullable Integer styleId) {
        Timber.d("navigateToStyle(%s)", styleId);

        Intent intent = new Intent(getContext(), ListActivity.class);
        intent.putExtra(NavigationProvider.PAGE_KEY, Page.STYLE.ordinal());
        intent.putExtra("style", String.valueOf(styleId));
        getContext().startActivity(intent);
    }

    private void navigateToCountry(@NonNull Country country) {
        Timber.d("navigateToCountry(%s)", country.getName());

        Intent intent = new Intent(getContext(), ListActivity.class);
        intent.putExtra(NavigationProvider.PAGE_KEY, Page.COUNTRY.ordinal());
        intent.putExtra("country", String.valueOf(country.getId()));
        getContext().startActivity(intent);
    }
}