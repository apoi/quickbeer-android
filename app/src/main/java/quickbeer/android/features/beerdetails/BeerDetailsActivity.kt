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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import com.squareup.picasso.Callback.EmptyCallback
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.collapsing_toolbar_activity.*
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events.Entry
import quickbeer.android.core.activity.BindingDrawerActivity
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.SimpleDataBinder
import quickbeer.android.core.viewmodel.ViewModel
import quickbeer.android.data.NoOlderThanMonth
import quickbeer.android.data.actions.BeerActions
import quickbeer.android.data.actions.BrewerActions
import quickbeer.android.data.pojos.Beer
import quickbeer.android.features.photoview.PhotoViewActivity
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.providers.ResourceProvider
import quickbeer.android.providers.ToastProvider
import quickbeer.android.transformations.BlurTransformation
import quickbeer.android.transformations.ContainerLabelExtractor
import quickbeer.android.utils.kotlin.isNumeric
import quickbeer.android.viewmodels.SearchViewViewModel
import timber.log.Timber
import javax.inject.Inject

class BeerDetailsActivity : BindingDrawerActivity() {

    @Inject
    internal lateinit var beerActions: BeerActions

    @Inject
    internal lateinit var brewerActions: BrewerActions

    @Inject
    internal lateinit var navigationProvider: NavigationProvider

    @Inject
    internal lateinit var toastProvider: ToastProvider

    @Inject
    internal lateinit var progressStatusProvider: ProgressStatusProvider

    @Inject
    internal lateinit var resourceProvider: ResourceProvider

    @Inject
    internal lateinit var searchViewViewModel: SearchViewViewModel

    @Inject
    internal lateinit var picasso: Picasso

    @Inject
    internal lateinit var analytics: Analytics

    private var beerId: Int = 0

    private var beerName: String = ""

    private var brewerName: String = ""

    private val dataBinder = object : SimpleDataBinder() {
        override fun bind(disposable: CompositeDisposable) {
            val sourceObservable = beerActions.get(beerId, NoOlderThanMonth())
                .subscribeOn(Schedulers.io())
                .filter { it.isOnNext }
                .map { it.value!! }
                .take(1)
                .publish()

            // Update beer access date
            disposable.add(sourceObservable
                .map { it.id }
                .subscribe({ beerActions.access(it) }, { Timber.e(it) }))

            // Update brewer access date
            disposable.add(sourceObservable
                .map { it.brewerId }
                .subscribe({ brewerActions.access(it!!) }, { Timber.e(it) }))

            // Set toolbar title
            disposable.add(
                sourceObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ setToolbarDetails(it) }, { Timber.e(it) }))

            // Update share intent
            disposable.add(
                sourceObservable
                    .subscribe({
                        beerName = it.name ?: ""
                        brewerName = it.brewerName ?: ""
                    }, { Timber.e(it) }))

            disposable.add(
                progressStatusProvider
                    .progressStatus()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ progress_indicator_bar.setProgress(it) }, { Timber.e(it) }))

            disposable.add(
                sourceObservable
                    .connect())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.collapsing_toolbar_activity)

        setupDrawerLayout(false)

        setBackNavigationEnabled(true)

        collapsing_toolbar_background.setOnClickListener { toastProvider.showToast(R.string.beer_details_no_photo) }

        if (savedInstanceState != null) {
            beerId = savedInstanceState.getInt("beerId")
        } else {
            if (Intent.ACTION_VIEW == intent.action) {
                val idSegment = intent.data.pathSegments.find { it.isNumeric() }
                if (idSegment != null) {
                    beerId = idSegment.toInt()
                    analytics.createEvent(Entry.LINK_BEER)
                }
            }

            if (beerId <= 0) {
                beerId = intent.getIntExtra("beerId", 0)
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.container, BeerDetailsPagerFragment.newInstance(beerId))
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.beer_menu, menu)
        return true
    }

    private fun setToolbarDetails(beer: Beer) {
        collapsing_toolbar.title = beer.name

        picasso.load(beer.imageUri())
            .transform(ContainerLabelExtractor(300, 300))
            .transform(BlurTransformation(applicationContext, 15))
            .into(collapsing_toolbar_background, object : EmptyCallback() {
                override fun onSuccess() {
                    toolbar_overlay_gradient.visibility = View.VISIBLE
                    collapsing_toolbar_background.setOnClickListener { openPhotoView(beer.imageUri()) }
                }
            })
    }

    private fun openPhotoView(uri: String) {
        val intent = Intent(this, PhotoViewActivity::class.java)
        intent.putExtra("source", uri)
        startActivity(intent)
    }

    private fun share() {
        val uri = String.format(Constants.BEER_PATH, beerId)
        val template = resourceProvider.getString(R.string.share_template)
        val text = String.format(template, beerName, brewerName, uri)
        val intent = ShareCompat.IntentBuilder.from(this)
            .setType("text/plain")
            .setText(text)
            .intent

        startActivity(Intent.createChooser(intent, resourceProvider.getString(R.string.share)))
    }

    private fun openInBrowser() {
        val uri = String.format(Constants.BEER_PATH, beerId)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(Intent.createChooser(intent, resourceProvider.getString(R.string.open)))
    }

    override fun inject() {
        getComponent().inject(this)
    }

    override fun viewModel(): ViewModel {
        return searchViewViewModel
    }

    override fun dataBinder(): DataBinder {
        return dataBinder
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("beerId", beerId)
        super.onSaveInstanceState(outState)
    }

    override fun navigateTo(menuItem: MenuItem) {
        navigationProvider.navigateWithNewActivity(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_item_share -> {
                share()
                return true
            }
            R.id.menu_item_open -> {
                openInBrowser()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        Timber.d("onBackPressed")

        if (navigationProvider.canNavigateBack()) {
            navigationProvider.navigateBack()
        } else {
            super.onBackPressed()
        }
    }
}
