package quickbeer.android.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import quickbeer.android.R
import quickbeer.android.databinding.MainActivityBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.navigation.setupWithNavController
import quickbeer.android.ui.recyclerview.DefaultRecycledPoolHolder
import quickbeer.android.ui.recyclerview.RecycledPoolHolder
import quickbeer.android.util.ktx.hideKeyboard
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(R.layout.main_activity),
    NavController.OnDestinationChangedListener,
    RecycledPoolHolder by DefaultRecycledPoolHolder() {

    private val binding by viewBinding(MainActivityBinding::bind)

    private var currentNavController: StateFlow<NavController>? = null

    private var pendingDestination: Destination? = null

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
            R.navigation.barcode_nav,
            R.navigation.profile_nav
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
        lifecycleScope.launchWhenStarted {
            controller.collectLatest {
                it.removeOnDestinationChangedListener(this@MainActivity)
                it.addOnDestinationChangedListener(this@MainActivity)
            }
        }

        currentNavController = controller
    }

    fun selectMainTab() {
        binding.mainBottomNav.selectedItemId = R.id.discover
    }

    fun setPendingDestination(destination: Destination) {
        pendingDestination = destination
    }

    fun getPendingDestination(): Destination? {
        return pendingDestination
            ?.also { pendingDestination = null }
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
        // Nothing to do now. Status bar shows, but it's fine in the
        // current fullscreen photo views.
    }
}
