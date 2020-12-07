package quickbeer.android.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

open class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    override fun onResume() {
        super.onResume()

        observeViewState()
    }

    open fun observeViewState() = Unit

    fun <T> observe(state: LiveData<T>, observer: (T) -> Unit) {
        state.observe(this, observer::invoke)
    }

    protected fun navigate(navDirections: NavDirections) {
        val navOptions = NavOptions.Builder()
            .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
            .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
            .build()

        findNavController()
            .navigate(navDirections, navOptions)
    }
}
