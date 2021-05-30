package quickbeer.android.feature

import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import kotlinx.coroutines.flow.StateFlow
import quickbeer.android.R
import quickbeer.android.databinding.MainActivityBinding
import quickbeer.android.navigation.NavParams
import quickbeer.android.navigation.setupWithNavController
import quickbeer.android.ui.recyclerview.DefaultRecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.util.ktx.hideKeyboard
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class MainActivity :
    AppCompatActivity(R.layout.main_activity),
    NavController.OnDestinationChangedListener,
    RecycledPoolHolder by DefaultRecycledPoolHolder() {

    private val binding by viewBinding(MainActivityBinding::bind)

    private var currentNavController: StateFlow<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
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
        observe(controller) {
            it.removeOnDestinationChangedListener(this)
            it.addOnDestinationChangedListener(this)
        }

        currentNavController = controller
    }

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        hideKeyboard()
        showNavBar(arguments?.getBoolean(NavParams.NAVBAR, true) ?: true)
        setFullscreen(arguments?.getBoolean(NavParams.FULLSCREEN, false) ?: false)
    }

    private fun showNavBar(showNavBar: Boolean) {
        if (binding.mainBottomNav.isVisible != showNavBar) {
            binding.mainBottomNav.isVisible = showNavBar
        }
    }

    private fun setFullscreen(showFullscreen: Boolean) {
        val isFullscreen = (window.attributes.flags and FLAG_FULLSCREEN) != 0
        when {
            isFullscreen == showFullscreen -> Unit
            showFullscreen -> window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
            else -> window.clearFlags(FLAG_FULLSCREEN)
        }
    }
}
