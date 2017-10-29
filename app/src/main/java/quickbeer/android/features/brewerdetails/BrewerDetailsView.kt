/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela></antti.poikela>@iki.fi>

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.features.brewerdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.StringRes
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.widget.Toast
import kotlinx.android.synthetic.main.brewer_details_fragment_details.view.*
import polanski.option.Option
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.LaunchAction
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.features.countrydetails.CountryDetailsActivity
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.providers.ToastProvider
import quickbeer.android.utils.StringUtils
import quickbeer.android.utils.StringUtils.emptyAsNone
import timber.log.Timber
import javax.inject.Inject

/**
 * View holder for all the brewer details
 */
class BrewerDetailsView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    private val VISIBLE = 1.0f
    private val OPAQUE = 0.2f

    @Inject
    internal lateinit var countryStore: CountryStore

    @Inject
    internal lateinit var resourceProvider: ResourceProvider

    @Inject
    internal lateinit var toastProvider: ToastProvider

    @Inject
    internal lateinit var analytics: Analytics

    override fun onFinishInflate() {
        super.onFinishInflate()

        (context as InjectingDrawerActivity)
                .component
                .inject(this)
    }

    fun setBrewer(brewer: Brewer) {
        ofObj(brewer.founded)
                .map { it.year.toString() }
                .orOption { notAvailableOption() }
                .ifSome { brewer_founded.text = it }

        ofObj(brewer.website)
                .filter { StringUtils.hasValue(it) }
                .map { StringUtils.removeTrailingSlash(it) }
                .map { StringUtils.addMissingProtocol(it) }
                .ifSome { website ->
                    brewer_website.alpha = VISIBLE
                    brewer_website_column.setOnClickListener { openUri(LaunchAction.BREWER_WEBSITE, website) }
                }
                .ifNone {
                    brewer_website.alpha = OPAQUE
                    brewer_website_column.setOnClickListener { showToast(R.string.brewer_details_no_website) }
                }

        ofObj(brewer.facebook)
                .filter { StringUtils.hasValue(it) }
                .map { String.format(Constants.FACEBOOK_PATH, it) }
                .ifSome { facebook ->
                    brewer_facebook.alpha = VISIBLE
                    brewer_facebook_column.setOnClickListener { openUri(LaunchAction.BREWER_FACEBOOK, facebook) }
                }
                .ifNone {
                    brewer_facebook.alpha = OPAQUE
                    brewer_facebook_column.setOnClickListener { showToast(R.string.brewer_details_no_facebook) }
                }

        ofObj(brewer.twitter)
                .filter { StringUtils.hasValue(it) }
                .map { String.format(Constants.TWITTER_PATH, it) }
                .ifSome { twitter ->
                    brewer_twitter.alpha = VISIBLE
                    brewer_twitter_column.setOnClickListener { openUri(LaunchAction.BREWER_TWITTER, twitter) }
                }
                .ifNone {
                    brewer_twitter.alpha = OPAQUE
                    brewer_twitter_column.setOnClickListener { showToast(R.string.brewer_details_no_twitter) }
                }

        ofObj(brewer.countryId)
                .map { countryStore.getItem(it) }
                .ifSome { (id) -> brewer_country_row.setOnClickListener { navigateToCountry(id) } }
                .map { it.name }
                .orOption { notAvailableOption() }
                .ifSome { brewer_country.text = it }

        ofObj(brewer.city)
                .filter { StringUtils.hasValue(it) }
                .ifSome { city -> brewer_city_row.setOnClickListener { openWikipedia(city) } }
                .orOption { notAvailableOption() }
                .ifSome { brewer_city.text = it }

        ofObj(brewer.address)
                .filter { StringUtils.hasValue(it) }
                .orOption { notAvailableOption() }
                .ifSome { brewer_address.text = it }

        fullAddress(brewer)
                .ifSome { address -> brewer_address_row.setOnClickListener { openMaps(address) } }
    }

    private fun notAvailableOption(): Option<String> {
        return ofObj(notAvailableString())
    }

    private fun notAvailableString(): String {
        return resourceProvider.getString(R.string.not_available)
    }

    private fun openUri(action: LaunchAction, uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        context.startActivity(intent)

        analytics.createEvent(action)
    }

    private fun navigateToCountry(countryId: Int) {
        Timber.d("navigateToCountry(%s)", countryId)

        val intent = Intent(context, CountryDetailsActivity::class.java)
        intent.putExtra(Constants.ID_KEY, countryId)
        context.startActivity(intent)
    }

    private fun openWikipedia(article: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constants.WIKIPEDIA_PATH, article)))
        context.startActivity(intent)
    }

    private fun openMaps(address: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constants.GOOGLE_MAPS_PATH, address)))
        context.startActivity(intent)
    }

    private fun fullAddress(brewer: Brewer): Option<String> {
        return ofObj(brewer.countryId)
                .map { countryStore.getItem(it) }
                .map { it.name }
                .lift(emptyAsNone(brewer.city), emptyAsNone(brewer.address)) { country, city, address ->
                    val street = if (address.contains(","))
                        address.split(",")[0]
                    else
                        address

                    String.format("%s, %s, %s", street, city, country)
                }
    }

    private fun showToast(@StringRes resource: Int) {
        toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}