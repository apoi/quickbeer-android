package quickbeer.android.ui.search

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.view.SearchView

abstract class SearchBarFragment(@LayoutRes layout: Int) : MainFragment(layout) {

    abstract val searchHint: Int

    private val closeOnBackHandler = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            searchView().closeSearchView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackHandler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView().apply {
            queryChangedCallback = ::onSearchQueryChanged
            querySubmitCallback = ::onSearchQuerySubmit
            searchFocusChangeCallback = ::onSearchFocusChanged
            navigateBackCallback = requireActivity()::onBackPressed
        }
    }

    protected abstract fun searchView(): SearchView

    protected open fun onSearchQueryChanged(query: String) = Unit

    protected open fun onSearchQuerySubmit(query: String) = Unit

    @CallSuper
    protected open fun onSearchFocusChanged(hasFocus: Boolean) {
        closeOnBackHandler.isEnabled = hasFocus
    }
}
