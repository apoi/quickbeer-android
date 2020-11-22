package quickbeer.android.ui.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.SearchView.OnSuggestionListener
import com.google.android.material.appbar.MaterialToolbar
import quickbeer.android.R
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.ui.listener.AnimationEndListener
import quickbeer.android.util.ktx.getThemeColor
import quickbeer.android.util.ktx.onGlobalLayout

abstract class SearchFragment(@LayoutRes layout: Int) : BaseFragment(layout) {

    abstract val searchHint: Int

    private val revealDuration = 300L
    private val iconWidth: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)
    }

    private val closeOnBackHandler = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            closeSearchView()
        }
    }

    protected abstract fun toolbar(): MaterialToolbar
    protected abstract fun searchViewModel(): SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, closeOnBackHandler)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireView().onGlobalLayout(::setupSearchView)
    }

    private fun setupSearchView() {
        val searchMenuItem = toolbar().menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView

        // Search may already be open after orientation changes
        if (searchMenuItem.isActionViewExpanded) {
            setWhiteToolbarBackground(toolbar())
            closeOnBackHandler.isEnabled = true
        }

        searchView.suggestionsAdapter = searchViewModel().getSearchAdapter(requireActivity())
        searchView.queryHint = getString(searchHint)

        searchMenuItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    animateSearchOpen(toolbar(), 1)
                    closeOnBackHandler.isEnabled = true
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    if (item.isActionViewExpanded) {
                        animateSearchClose(toolbar(), 1)
                    }
                    closeOnBackHandler.isEnabled = false
                    return true
                }
            })

        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                searchViewModel().onSearchChanged(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewModel().onSearchSubmit(query)
                return true
            }
        })

        searchView.setOnSuggestionListener(object : OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                closeSearchView()
                searchView.onQuerySelected(searchViewModel().getSuggestionText(position))
                searchViewModel().onSuggestionClicked(position)
                return true
            }
        })
    }

    protected fun closeSearchView(): Boolean {
        return toolbar().menu.findItem(R.id.action_search).collapseActionView()
    }

    private fun animateSearchOpen(toolbar: MaterialToolbar, iconIndex: Int) {
        setWhiteToolbarBackground(toolbar)

        val startX = toolbar.width - iconWidth * iconIndex / 2
        val startY = toolbar.height / 2

        ViewAnimationUtils
            .createCircularReveal(toolbar, startX, startY, 0.0f, startX.toFloat())
            .setDuration(revealDuration)
            .start()
    }

    private fun animateSearchClose(toolbar: MaterialToolbar, iconIndex: Int) {
        setWhiteToolbarBackground(toolbar)

        val start = toolbar.width - iconWidth * iconIndex / 2
        val startY = toolbar.height / 2

        val endListener = AnimationEndListener {
            toolbar.setBackgroundColor(requireContext().getThemeColor(R.attr.colorPrimary))
        }

        ViewAnimationUtils
            .createCircularReveal(toolbar, start, startY, start.toFloat(), 0.0f)
            .setDuration(revealDuration)
            .apply { addListener(endListener) }
            .start()
    }

    private fun setWhiteToolbarBackground(toolbar: MaterialToolbar) {
        toolbar.apply {
            setBackgroundColor(resources.getColor(R.color.white, null))
            setCollapseIcon(R.drawable.ic_back)
            collapseIcon?.setTint(resources.getColor(R.color.icon_dark, null))
        }
    }
}
