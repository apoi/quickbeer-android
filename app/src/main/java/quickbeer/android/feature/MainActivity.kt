package quickbeer.android.feature

import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import quickbeer.android.R
import quickbeer.android.databinding.MainActivityBinding
import quickbeer.android.navigation.NavParams
import quickbeer.android.navigation.setupWithNavController
import quickbeer.android.ui.recyclerview.DefaultRecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.util.ktx.viewBinding

class MainActivity :
    AppCompatActivity(R.layout.main_activity),
    RecycledPoolHolder by DefaultRecycledPoolHolder() {

    private val binding by viewBinding(MainActivityBinding::bind)

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            decorView.systemUiVisibility = decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

            statusBarColor = resources.getColor(android.R.color.transparent, null)
        }

        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        // Setup the bottom navigation view with a list of navigation graphs
        val graphs = listOf(
            R.navigation.discover_nav,
            R.navigation.details_beer_nav,
            R.navigation.more_nav
        )

        val controller = binding.mainBottomNav.setupWithNavController(
            navGraphIds = graphs,
            fragmentManager = supportFragmentManager,
            containerId = R.id.main_nav_container,
            intent = intent
        )

        // Clear intent after navigation
        intent.data = null

        // Whenever the selected controller changes, setup action bar and other changes
        controller.observe(this, this::setupFullscreenHandler)
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun setupFullscreenHandler(navController: NavController) {
        navController.removeOnDestinationChangedListener(fullscreenListener)
        navController.addOnDestinationChangedListener(fullscreenListener)
    }

    private val fullscreenListener = NavController.OnDestinationChangedListener { _, _, arguments ->
        showNavBar(arguments?.getBoolean(NavParams.NAVBAR, true) ?: true)
        setFullscreen(arguments?.getBoolean(NavParams.FULLSCREEN, false) ?: false)
    }

    private fun showNavBar(navbar: Boolean) {
        binding.mainBottomNav.isVisible = navbar
    }

    private fun setFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            window.setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
