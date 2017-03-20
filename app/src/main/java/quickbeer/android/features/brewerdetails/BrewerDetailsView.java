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

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import polanski.option.Option;
import quickbeer.android.R;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.utils.Countries;
import quickbeer.android.utils.StringUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

/**
 * View holder for all the brewer details
 */
public class BrewerDetailsView extends NestedScrollView {

    @BindView(R.id.brewer_description)
    TextView brewerDescription;

    @BindView(R.id.brewer_founded_column)
    View brewerFoundedColumn;

    @BindView(R.id.brewer_founded)
    TextView brewerFounded;

    @BindView(R.id.brewer_location)
    TextView locationTextView;

    @BindView(R.id.brewer_location_row)
    View brewerLocationRow;

    @Nullable
    @Inject
    Countries countries;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    public BrewerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this, this);

        ((InjectingDrawerActivity) getContext())
                .getComponent()
                .inject(this);
    }

    public void setBrewer(@NonNull Brewer brewer) {
        checkNotNull(countries);
        checkNotNull(resourceProvider);

        brewerDescription.setText(
                StringUtils.value(brewer.description(),
                        resourceProvider.getString(R.string.no_description)));

        ofObj(brewer.opened())
                .map(ZonedDateTime::getYear)
                .ifSome(brewerFounded::setText);

        ofObj(brewer.countryId())
                .map(countries::getItem)
                .ifSome(country -> brewerLocationRow.setOnClickListener(__ -> navigateToCountry(country)))
                .map(Country::getName)
                .map(country -> String.format("%s, %s", brewer.city(), country))
                .orOption(this::notAvailableString)
                .ifSome(locationTextView::setText);
    }

    @NonNull
    private Option<String> notAvailableString() {
        return ofObj(get(resourceProvider).getString(R.string.not_available));
    }

    private void navigateToCountry(@NonNull Country country) {
        Timber.d("navigateToCountry(%s)", country.getName());

        Intent intent = new Intent(getContext(), ListActivity.class);
        intent.putExtra(NavigationProvider.PAGE_KEY, Page.COUNTRY.ordinal());
        intent.putExtra("country", String.valueOf(country.getId()));
        getContext().startActivity(intent);
    }

    private void showToast(@StringRes int resource) {
        get(toastProvider).showCancelableToast(resource, Toast.LENGTH_LONG);
    }

}