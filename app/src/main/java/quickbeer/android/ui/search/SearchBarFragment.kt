package quickbeer.android.ui.search

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.util.ktx.onGlobalLayout

abstract class SearchBarFragment(@LayoutRes layout: Int) : MainFragment(layout) {

    abstract val searchHint: Int

    private val closeOnBackHandler = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            searchView().closeSearchView()
        }
    }

    protected abstract fun searchView(): SearchView

    protected abstract fun searchViewModel(): SearchBarInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackHandler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().onGlobalLayout(::setupSearchView)
    }

    private fun setupSearchView() {
        searchView().apply {
            setAdapter(searchViewModel().getSearchAdapter())
            searchFocusChangeCallback = { closeOnBackHandler.isEnabled = it }
            queryChangedCallback = searchViewModel()::onSearchChanged
            querySubmitCallback = searchViewModel()::onSearchSubmit
            navigateBackCallback = requireActivity()::onBackPressed
        }
    }
}
