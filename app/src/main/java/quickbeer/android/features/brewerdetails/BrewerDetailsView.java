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
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.threeten.bp.ZonedDateTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import polanski.option.Option;
import quickbeer.android.Constants;
import quickbeer.android.R;
import quickbeer.android.analytics.Analytics;
import quickbeer.android.analytics.Events.LaunchAction;
import quickbeer.android.core.activity.InjectingDrawerActivity;
import quickbeer.android.data.pojos.Brewer;
import quickbeer.android.data.pojos.Country;
import quickbeer.android.data.stores.CountryStore;
import quickbeer.android.features.list.ListActivity;
import quickbeer.android.providers.NavigationProvider;
import quickbeer.android.providers.NavigationProvider.Page;
import quickbeer.android.providers.ResourceProvider;
import quickbeer.android.providers.ToastProvider;
import quickbeer.android.utils.StringUtils;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

/**
 * View holder for all the brewer details
 */
public class BrewerDetailsView extends NestedScrollView {

    @BindView(R.id.brewer_founded_column)
    View brewerFoundedColumn;

    @BindView(R.id.brewer_founded)
    TextView brewerFounded;

    @BindView(R.id.brewer_website_column)
    View brewerWebsiteColumn;

    @BindView(R.id.brewer_website)
    ImageView brewerWebsite;

    @BindView(R.id.brewer_facebook_column)
    View brewerFacebookColumn;

    @BindView(R.id.brewer_facebook)
    ImageView brewerFacebook;

    @BindView(R.id.brewer_twitter_column)
    View brewerTwitterColumn;

    @BindView(R.id.brewer_twitter)
    ImageView brewerTwitter;

    @BindView(R.id.brewer_country_row)
    View brewerCountryRow;

    @BindView(R.id.brewer_country)
    TextView brewerCountry;

    @BindView(R.id.brewer_city_row)
    View brewerCityRow;

    @BindView(R.id.brewer_city)
    TextView brewerCity;

    @BindView(R.id.brewer_address_row)
    View brewerAddressRow;

    @BindView(R.id.brewer_address)
    TextView brewerAddress;

    @Nullable
    @Inject
    CountryStore countryStore;

    @Nullable
    @Inject
    ResourceProvider resourceProvider;

    @Nullable
    @Inject
    ToastProvider toastProvider;

    @Nullable
    @Inject
    Analytics analytics;

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
        checkNotNull(countryStore);
        checkNotNull(resourceProvider);

        ofObj(brewer.founded())
                .map(ZonedDateTime::getYear)
                .map(String::valueOf)
                .orOption(this::notAvailableOption)
                .ifSome(brewerFounded::setText);

        ofObj(brewer.website())
                .filter(StringUtils::hasValue)
                .map(StringUtils::removeTrailingSlash)
                .map(StringUtils::addMissingProtocol)
                .ifSome(website -> {
                    brewerWebsite.setAlpha(1.0f);
                    brewerWebsiteColumn.setOnClickListener(__ -> openUri(LaunchAction.BREWER_WEBSITE, website));
                })
                .ifNone(() -> {
                    brewerWebsite.setAlpha(0.2f);
                    brewerWebsiteColumn.setOnClickListener(__ -> showToast(R.string.brewer_details_no_website));
                });

        ofObj(brewer.facebook())
                .filter(StringUtils::hasValue)
                .map(handle -> String.format(Constants.FACEBOOK_PATH, handle))
                .ifSome(facebook -> {
                    brewerFacebook.setAlpha(1.0f);
                    brewerFacebookColumn.setOnClickListener(__ -> openUri(LaunchAction.BREWER_FACEBOOK, facebook));
                })
                .ifNone(() -> {
                    brewerFacebook.setAlpha(0.2f);
                    brewerFacebookColumn.setOnClickListener(__ -> showToast(R.string.brewer_details_no_facebook));
                });

        ofObj(brewer.twitter())
                .filter(StringUtils::hasValue)
                .map(handle -> String.format(Constants.TWITTER_PATH, handle))
                .ifSome(twitter -> {
                    brewerTwitter.setAlpha(1.0f);
                    brewerTwitterColumn.setOnClickListener(__ -> openUri(LaunchAction.BREWER_TWITTER, twitter));
                })
                .ifNone(() -> {
                    brewerTwitter.setAlpha(0.2f);
                    brewerTwitterColumn.setOnClickListener(__ -> showToast(R.string.brewer_details_no_twitter));
                });

        ofObj(brewer.countryId())
                .map(countryStore::getItem)
                .ifSome(country -> brewerCountryRow.setOnClickListener(__ -> navigateToCountry(country)))
                .map(Country::getName)
                .orOption(this::notAvailableOption)
                .ifSome(brewerCountry::setText);

        ofObj(brewer.city())
                .filter(StringUtils::hasValue)
                .orOption(this::notAvailableOption)
                .ifSome(brewerCity::setText);

        ofObj(brewer.address())
                .filter(StringUtils::hasValue)
                .orOption(this::notAvailableOption)
                .ifSome(brewerAddress::setText);
    }

    @NonNull
    private Option<String> notAvailableOption() {
        return ofObj(notAvailableString());
    }

    @NonNull
    private String notAvailableString() {
        return get(resourceProvider).getString(R.string.not_available);
    }

    private void openUri(@NonNull LaunchAction action, @NonNull String uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        getContext().startActivity(intent);

        get(analytics).createEvent(action);
    }

    private void navigateToCountry(@NonNull Country country) {
        Timber.d("navigateToCountry(%s)", country.getName());

        Intent intent = new Intent(getContext(), ListActivity.class);
        intent.putExtra(NavigationProvider.PAGE_KEY, Page.COUNTRY.ordinal());
        intent.putExtra("countryId", country.getId());
        getContext().startActivity(intent);
    }

    private void showToast(@StringRes int resource) {
        get(toastProvider).showCancelableToast(resource, Toast.LENGTH_LONG);
    }

}