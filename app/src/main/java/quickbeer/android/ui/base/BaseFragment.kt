package quickbeer.android.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import quickbeer.android.R
import quickbeer.android.navigation.Destination

abstract class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    override fun onResume() {
        super.onResume()

        observeViewState()
    }

    open fun observeViewState() = Unit

    protected fun navigate(navDirections: NavDirections) {
        findNavController()
            .navigate(navDirections, navOptions())
    }

    protected fun navigate(destination: Destination) {
        findNavController()
            .navigate(destination.uri, navOptions())
    }

    private fun navOptions(): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.enter_anim)
            .setExitAnim(R.anim.exit_anim)
            .setPopEnterAnim(R.anim.pop_enter_anim)
            .setPopExitAnim(R.anim.pop_exit_anim)
            .build()
    }
}
