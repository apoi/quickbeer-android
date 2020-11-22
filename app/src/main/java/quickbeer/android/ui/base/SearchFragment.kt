package quickbeer.android.ui.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import quickbeer.android.R
import quickbeer.android.ui.listener.AnimationEndListener
import quickbeer.android.util.ktx.getThemeColor
import quickbeer.android.util.ktx.onGlobalLayout

abstract class SearchFragment(@LayoutRes layout: Int) : BaseFragment(layout) {

    abstract val searchHint: Int
    abstract val toolbar: MaterialToolbar
    abstract val searchViewModel: SearchViewModel

    private val iconWidth: Int by lazy {
        resources.getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)
    }

    private val revealDuration = 300L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.onGlobalLayout(::setupSearchView)
    }

    private fun setupSearchView() {
        val searchMenuItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView

        // Search may already be open after orientation changes
        if (searchMenuItem.isActionViewExpanded) {
            setWhiteToolbarBackground()
        }

        searchView.suggestionsAdapter = searchViewModel.getSearchAdapter(requireActivity())
        searchView.queryHint = getString(searchHint)

        searchMenuItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    animateSearchOpen(toolbar, 1)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    if (item.isActionViewExpanded) {
                        animateSearchClose(toolbar, 1)
                    }
                    return true
                }
            })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                searchViewModel.onSearchChanged(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewModel.onSearchSubmit(query)
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                searchViewModel.onSuggestionClicked(position)
                return true
            }
        })
    }

    private fun animateSearchOpen(toolbar: MaterialToolbar, iconIndex: Int) {
        setWhiteToolbarBackground()

        val startX = toolbar.width - iconWidth * iconIndex / 2
        val startY = toolbar.height / 2

        ViewAnimationUtils
            .createCircularReveal(toolbar, startX, startY, 0.0f, startX.toFloat())
            .setDuration(revealDuration)
            .start()
    }

    private fun animateSearchClose(toolbar: MaterialToolbar, iconIndex: Int) {
        setWhiteToolbarBackground()

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

    private fun setWhiteToolbarBackground() {
        toolbar.setBackgroundColor(resources.getColor(R.color.white, null))
        toolbar.setCollapseIcon(R.drawable.ic_back)
        toolbar.collapseIcon?.setTint(resources.getColor(R.color.icon_dark, null))
    }
}
