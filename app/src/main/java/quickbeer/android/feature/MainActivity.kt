package quickbeer.android.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.MainActivityBinding
import quickbeer.android.navigation.Destination
import quickbeer.android.navigation.NavParams
import quickbeer.android.ui.base.Resetable
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

    private lateinit var navController: NavController

    private var pendingDestination: Destination? = null

    var suppressNavAnimation: Boolean = false

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
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_container) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val bottomNavigationView = binding.mainBottomNav
        bottomNavigationView.setupWithNavController(navController)

        // Tab select handler to suppress default animation on tab changes
        bottomNavigationView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            suppressNavAnimation = true
            true
        }

        // Tab reselect handler to jump to the root view of the tab
        bottomNavigationView.setOnItemReselectedListener { item ->
            val selectedMenuItemNavGraph = navHostFragment.navController
                .graph.findNode(item.itemId) as? NavGraph?

            val previousBackStackEntry = navHostFragment.navController
                .previousBackStackEntry?.destination?.id

            if (previousBackStackEntry == null) {
                // We're already at the beginning, scroll views here on reselect
                val currentFragment = navHostFragment.childFragmentManager.fragments.first()
                (currentFragment as? Resetable)?.onReset()
            } else {
                selectedMenuItemNavGraph?.let { menuGraph ->
                    navHostFragment.navController
                        .popBackStack(menuGraph.startDestinationId, false)
                }
            }
        }
    }

    fun selectMainTab() {
        binding.mainBottomNav.selectedItemId = R.id.search
    }

    fun setPendingDestination(destination: Destination) {
        pendingDestination = destination
    }

    fun getPendingDestination(): Destination? {
        return pendingDestination
            ?.also { pendingDestination = null }
    }

    override fun onBackPressed() {
        if (!onBackNavigation()) super.onBackPressed()
    }

    private fun onBackNavigation(): Boolean {
        val bottomNavigationView = binding.mainBottomNav

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_container) as NavHostFragment

        val previousBackStackEntry = navHostFragment.navController
            .previousBackStackEntry?.destination?.id

        return if (previousBackStackEntry != R.id.recent_fragment) {
            // Navigate back if next item in back stack isn't yet Recent (search) fragment.
            // This handles back navigation in non-Recent tabs
            onSupportNavigateUp()
        } else if (bottomNavigationView.selectedItemId == R.id.search) {
            // Navigate back if current tab is Search. This handles back navigation
            // in the Search tab.
            onSupportNavigateUp()
            true
        } else {
            // Switch to Search tab when back navigation would take back to initial state.
            // This handles tab switching to Search with keeping the expected navigation status:
            // otherwise, the default handling would show the initial fragment of Search.
            selectMainTab()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
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
