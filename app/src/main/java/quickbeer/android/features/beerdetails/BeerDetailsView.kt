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
package quickbeer.android.features.beerdetails

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AlertDialog
import android.util.AttributeSet
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import kotlinx.android.synthetic.main.beer_details_fragment_details.view.*
import polanski.option.Option
import polanski.option.Option.ofObj
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.User
import quickbeer.android.data.stores.BeerStyleStore
import quickbeer.android.data.stores.CountryStore
import quickbeer.android.features.brewerdetails.BrewerDetailsActivity
import quickbeer.android.features.countrydetails.CountryDetailsActivity
import quickbeer.android.features.profile.ProfileActivity
import quickbeer.android.features.styledetails.StyleDetailsActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.providers.ToastProvider
import quickbeer.android.utils.kotlin.formatDateTime
import quickbeer.android.utils.kotlin.orDefault
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

/**
 * View holder for all the beer details
 */
class BeerDetailsView(context: Context, attrs: AttributeSet) : NestedScrollView(context, attrs) {

    @Inject
    internal lateinit var countryStore: CountryStore

    @Inject
    internal lateinit var beerStyleStore: BeerStyleStore

    @Inject
    internal lateinit var resourceProvider: ResourceProvider

    @Inject
    internal lateinit var toastProvider: ToastProvider

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    override fun onFinishInflate() {
        super.onFinishInflate()

        (context as InjectingDrawerActivity)
            .getComponent()
            .inject(this)

        beer_rating_overall_column.setOnClickListener { showToast(R.string.description_rating_overall) }
        beer_rating_style_column.setOnClickListener { showToast(R.string.description_rating_style) }
        beer_abv_column.setOnClickListener { showToast(R.string.description_abv) }
        beer_ibu_column.setOnClickListener { showToast(R.string.description_ibu) }
        rating_card_overlay.setOnClickListener { showLoginDialog() }
    }

    private fun showLoginDialog() {
        AlertDialog.Builder(context)
            .setTitle(R.string.login_dialog_title)
            .setMessage(R.string.login_to_rate_message)
            .setPositiveButton(R.string.ok) { _, _ -> navigateToLogin() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    fun setUser(user: User) {
        ofObj(user).ifSome { rating_card_overlay.visibility = View.GONE }
    }

    fun setBeer(beer: Beer) {
        beer_description.text = beer.description.orDefault(resourceProvider.getString(R.string.no_description))

        beer.brewerName?.let { brewer_name.text = it }
        beer.brewerId?.let { brewerId -> brewer_name_row.setOnClickListener { navigateToBrewer(brewerId) } }

        ofObj(beer.styleId)
            .map { beerStyleStore.getItem(it) }
            .orOption { ofObj(beer.styleName).flatMap { beerStyleStore.getStyle(it) } }
            .ifSome { (id) -> beer_style_row.setOnClickListener { navigateToStyle(id) } }
            .map { it.name }
            .orOption { notAvailableString() }
            .ifSome { beer_style.text = it }

        ofObj(beer.overallRating)
            .filter { it > 0 }
            .map { Math.round(it).toString() }
            .ifSome { beer_rating_overall.text = it }
            .ifNone {
                beer_rating_overall.text = "?"
                beer_rating_overall_column.setOnClickListener { showToast(R.string.not_enough_ratings) }
            }

        ofObj(beer.styleRating)
            .filter { it > 0 }
            .map { Math.round(it).toString() }
            .ifSome { beer_rating_style.text = it }
            .ifNone {
                beer_rating_style.text = "?"
                beer_rating_style_column.setOnClickListener { showToast(R.string.not_enough_ratings) }
            }

        ofObj(beer.alcohol)
            .filter { it > 0 }
            .map { String.format(Locale.ROOT, "%.1f%%", it) }
            .orOption { notAvailableString() }
            .ifSome { beer_abv.text = it }

        ofObj(beer.ibu)
            .map { Math.round(it).toString() }
            .orOption { notAvailableString() }
            .ifSome { beer_ibu.text = it }

        ofObj(beer.tickValue)
            .filter { beer.isTicked() }
            .orOption { ofObj(0) }
            .ifSome { rating_bar.rating = it.toFloat() }

        ofObj(beer.tickDate)
            .filter { beer.isTicked() }
            .map { it.formatDateTime(resourceProvider.getString(R.string.beer_tick_date)) }
            .ifSome { value ->
                ticked_date.text = value
                ticked_date.visibility = View.VISIBLE
            }
            .ifNone { ticked_date.visibility = View.GONE }
    }

    fun setBrewer(brewer: Brewer) {
        ofObj(brewer.countryId)
            .map { countryStore.getItem(it) }
            .ifSome { (id) -> brewer_country_row.setOnClickListener { navigateToCountry(id) } }
            .map { String.format("%s, %s", brewer.city, it.name) }
            .orOption { notAvailableString() }
            .ifSome { brewer_country.text = it }
    }

    fun setRatingBarChangeListener(listener: RatingBar.OnRatingBarChangeListener) {
        rating_bar.onRatingBarChangeListener = listener
    }

    private fun notAvailableString(): Option<String> {
        return ofObj(resourceProvider.getString(R.string.not_available))
    }

    private fun navigateToStyle(styleId: Int) {
        Timber.d("navigateToStyle(%s)", styleId)

        val intent = Intent(context, StyleDetailsActivity::class.java)
        intent.putExtra(Constants.ID_KEY, styleId)
        context.startActivity(intent)
    }

    private fun navigateToCountry(countryId: Int) {
        Timber.d("navigateToCountry(%s)", countryId)

        val intent = Intent(context, CountryDetailsActivity::class.java)
        intent.putExtra(Constants.ID_KEY, countryId)
        context.startActivity(intent)
    }

    private fun navigateToBrewer(brewerId: Int) {
        Timber.d("navigateToBrewer(%s)", brewerId)

        val intent = Intent(context, BrewerDetailsActivity::class.java)
        intent.putExtra("brewerId", brewerId)
        context.startActivity(intent)
    }

    private fun navigateToLogin() {
        Timber.d("navigateToLogin")

        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
    }

    private fun showToast(@StringRes resource: Int) {
        toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}