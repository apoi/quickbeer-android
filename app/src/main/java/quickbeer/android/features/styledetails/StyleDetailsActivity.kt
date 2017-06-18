package quickbeer.android.features.styledetails

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import quickbeer.android.R
import quickbeer.android.analytics.Analytics
import quickbeer.android.analytics.Events
import quickbeer.android.core.activity.InjectingDrawerActivity
import quickbeer.android.data.pojos.Style
import quickbeer.android.features.beerdetails.BeerDetailsPagerFragment
import quickbeer.android.providers.NavigationProvider
import quickbeer.android.providers.ProgressStatusProvider
import quickbeer.android.views.ProgressIndicatorBar
import timber.log.Timber

import io.reark.reark.utils.Preconditions.checkNotNull
import io.reark.reark.utils.Preconditions.get

class StyleDetailsActivity : InjectingDrawerActivity() {

    @BindView(R.id.collapsing_toolbar)
    lateinit var collapsingToolbar: CollapsingToolbarLayout

    @BindView(R.id.collapsing_toolbar_background)
    lateinit var imageView: ImageView

    @BindView(R.id.toolbar_overlay_gradient)
    lateinit var overlay: View

    @BindView(R.id.progress_indicator_bar)
    lateinit var progressIndicatorBar: ProgressIndicatorBar

    @Inject
    lateinit var navigationProvider: NavigationProvider

    @Inject
    lateinit var progressStatusProvider: ProgressStatusProvider

    @Inject
    lateinit var analytics: Analytics

    private var styleId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.collapsing_toolbar_activity)

        ButterKnife.bind(this)

        setupDrawerLayout(false)

        setBackNavigationEnabled(true)

        if (savedInstanceState != null) {
            styleId = savedInstanceState.getInt("styleId")
        } else {
            val intent = intent
            val action = intent.action

            if (Intent.ACTION_VIEW == action) {
                val segments = intent.data.pathSegments
                if (segments.size > 2) {
                    styleId = Integer.valueOf(segments[2])!!
                    get(analytics).createEvent(Events.Entry.LINK_STYLE)
                }
            }

            if (styleId <= 0) {
                styleId = intent.getIntExtra("styleId", 0)
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.container, BeerDetailsPagerFragment.newInstance(styleId))
                    .commit()
        }
    }

    private fun setToolbarDetails(style: Style) {
        collapsingToolbar.title = style.name
    }

    override fun inject() {
        component.inject(this)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("styleId", styleId)
        super.onSaveInstanceState(outState)
    }

    override fun navigateTo(menuItem: MenuItem) {
        navigationProvider.navigateWithNewActivity(menuItem)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
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
