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
import android.support.v7.app.AlertDialog;
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
import quickbeer.android.Constants;
import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.pojos.Beer;
import quickbeer.android.data.pojos.BeerStyle;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.pojos.User;
import quickbeer.android.data.stores.BeerStyleStore;
import quickbeer.android.data.stores.CountryStore;
import quickbeer.android.features.brewerdetails.BrewerDetailsActivity;
import quickbeer.android.features.countrydetails.CountryDetailsActivity;
import quickbeer.android.features.profile.ProfileActivity;
import quickbeer.android.features.styledetails.StyleDetailsActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.utils.DateUtils;
import quickbeer.android.utils.StringUtils;
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

    @BindView(R.id.beer_style_row)
    View beerStyleRow;

    @BindView(R.id.beer_style)
    TextView beerStyle;

    @BindView(R.id.brewer_name_row)
    View brewerNameRow;

    @BindView(R.id.brewer_name)
    TextView brewerName;

    @BindView(R.id.brewer_country_row)
    View brewerLocationRow;

    @BindView(R.id.brewer_country)
    TextView brewerLocation;

    @BindView(R.id.beer_rating_overall_column)
    View overallRatingColumn;

    @BindView(R.id.beer_rating_overall)
    TextView overallRatingTextView;

    @BindView(R.id.beer_rating_style_column)
    View styleRatingColumn;

    @BindView(R.id.beer_rating_style)
    TextView styleRatingTextView;

    @BindView(R.id.beer_abv_column)
    View abvColumn;

    @BindView(R.id.beer_abv)
    TextView abvTextView;

    @BindView(R.id.beer_ibu_column)
    View ibuColumn;

    @BindView(R.id.beer_ibu)
    TextView ibuTextView;

    @BindView(R.id.rating_bar)
    RatingBar ratingBar;

    @BindView(R.id.ticked_date)
    TextView tickedDate;

    @BindView(R.id.rating_card_overlay)
    View ratingCardOverlay;

    @Nullable
    @Inject
    CountryStore countryStore;

    @Nullable
    @Inject
    BeerStyleStore beerStyleStore;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    @Nullable
    @Inject
    NavigationProvider navigationProvider;

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

        ratingCardOverlay.setOnClickListener(__ -> showLoginDialog());
    }

    private void showLoginDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.login_dialog_title)
                .setMessage(R.string.login_to_rate_message)
                .setPositiveButton(R.string.ok, (dialog, which) -> navigateToLogin())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel())
                .show();
    }

    public void setUser(@NonNull User user) {
        ofObj(user).ifSome(__ -> ratingCardOverlay.setVisibility(GONE));
    }

    public void setBeer(@NonNull Beer beer) {
        checkNotNull(beer);
        checkNotNull(beerStyleStore);
        checkNotNull(resourceProvider);

        descriptionTextView.setText(
                StringUtils.value(beer.description(),
                resourceProvider.getString(R.string.no_description)));

        ofObj(beer.brewerName())
                .ifSome(brewerName::setText);

        ofObj(beer.brewerId())
                .ifSome(brewerId -> brewerNameRow.setOnClickListener(__ -> navigateToBrewer(brewerId)));

        ofObj(beer.styleId())
                .map(beerStyleStore::getItem)
                .orOption(() -> ofObj(beer.styleName()).flatMap(beerStyleStore::getStyle))
                .ifSome(style -> beerStyleRow.setOnClickListener(__ -> navigateToStyle(style.getId())))
                .map(BeerStyle::getName)
                .orOption(this::notAvailableString)
                .ifSome(beerStyle::setText);

        ofObj(beer.overallRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .ifSome(overallRatingTextView::setText)
                .ifNone(() -> {
                    overallRatingTextView.setText("?");
                    overallRatingColumn.setOnClickListener(__ -> showToast(R.string.not_enough_ratings));
                });

        ofObj(beer.styleRating())
                .filter(value -> value > 0)
                .map(value -> String.valueOf(Math.round(value)))
                .ifSome(styleRatingTextView::setText)
                .ifNone(() -> {
                    styleRatingTextView.setText("?");
                    styleRatingColumn.setOnClickListener(__ -> showToast(R.string.not_enough_ratings));
                });

        ofObj(beer.alcohol())
                .map(value -> String.format(Locale.ROOT, "%.1f%%", value))
                .orOption(this::notAvailableString)
                .ifSome(abvTextView::setText);

        ofObj(beer.ibu())
                .map(value -> String.valueOf(Math.round(value)))
                .orOption(this::notAvailableString)
                .ifSome(ibuTextView::setText);

        ofObj(beer.tickValue())
                .filter(__ -> beer.isTicked())
                .ifSome(ratingBar::setRating)
                .ifNone(() -> ratingBar.setRating(0));

        ofObj(beer.tickDate())
                .filter(__ -> beer.isTicked())
                .map(date -> DateUtils.formatDateTime(resourceProvider.getString(R.string.beer_tick_date), date))
                .ifSome(value -> {
                    tickedDate.setText(value);
                    tickedDate.setVisibility(View.VISIBLE);
                })
                .ifNone(() -> tickedDate.setVisibility(GONE));
    }

    public void setBrewer(@NonNull Brewer brewer) {
        checkNotNull(countryStore);

        ofObj(brewer.countryId())
                .map(countryStore::getItem)
                .ifSome(country -> brewerLocationRow.setOnClickListener(__ -> navigateToCountry(country.getId())))
                .map(Country::getName)
                .map(country -> String.format("%s, %s", brewer.city(), country))
                .orOption(this::notAvailableString)
                .ifSome(brewerLocation::setText);
    }

    public void setRatingBarChangeListener(@NonNull RatingBar.OnRatingBarChangeListener listener) {
        ratingBar.setOnRatingBarChangeListener(listener);
    }

    @NonNull
    private Option<String> notAvailableString() {
        return ofObj(get(resourceProvider).getString(R.string.not_available));
    }

    @NonNull
    private static Option<String> notEnoughRatingsString() {
        return ofObj("?");
    }

    private void navigateToStyle(int styleId) {
        Timber.d("navigateToStyle(%s)", styleId);

        Intent intent = new Intent(getContext(), StyleDetailsActivity.class);
        intent.putExtra(Constants.ID_KEY, styleId);
        getContext().startActivity(intent);
    }

    private void navigateToCountry(int countryId) {
        Timber.d("navigateToCountry(%s)", countryId);

        Intent intent = new Intent(getContext(), CountryDetailsActivity.class);
        intent.putExtra(Constants.ID_KEY, countryId);
        getContext().startActivity(intent);
    }

    private void navigateToBrewer(int brewerId) {
        Timber.d("navigateToBrewer(%s)", brewerId);

        Intent intent = new Intent(getContext(), BrewerDetailsActivity.class);
        intent.putExtra("brewerId", brewerId);
        getContext().startActivity(intent);
    }

    private void navigateToLogin() {
        Timber.d("navigateToLogin");

        Intent intent = new Intent(getContext(), ProfileActivity.class);
        getContext().startActivity(intent);
    }

    private void showToast(@StringRes int resource) {
        get(toastProvider).showCancelableToast(resource, Toast.LENGTH_LONG);
    }

}