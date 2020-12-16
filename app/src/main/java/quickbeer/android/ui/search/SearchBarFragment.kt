package quickbeer.android.ui.search

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.searchview.widget.SearchView

abstract class SearchBarFragment(@LayoutRes layout: Int) : MainFragment(layout) {

    abstract val searchHint: Int

    private val closeOnBackHandler = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            searchView().closeSearchView()
        }
    }

    protected abstract fun searchView(): SearchView

    protected abstract fun searchActionsHandler(): SearchActionsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackHandler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView().apply {
            searchFocusChangeCallback = { closeOnBackHandler.isEnabled = it }
            queryChangedCallback = searchActionsHandler()::onSearchChanged
            querySubmitCallback = searchActionsHandler()::onSearchSubmit
            navigateBackCallback = requireActivity()::onBackPressed
        }
    }
}
