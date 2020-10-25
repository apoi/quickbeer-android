package quickbeer.android.ui.base

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

open class BaseFragment(@LayoutRes layout: Int) : Fragment(layout) {

    override fun onResume() {
        super.onResume()

        observeViewState()
    }

    open fun observeViewState() = Unit

    fun <T> observe(state: LiveData<T>, observer: (T) -> Unit) {
        state.observe(this, observer::invoke)
    }
}
