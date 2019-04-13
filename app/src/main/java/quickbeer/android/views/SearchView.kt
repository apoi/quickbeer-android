/**
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.views

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.Toast
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import kotlinx.android.synthetic.main.toolbar_search_view.view.*
import quickbeer.android.R
import quickbeer.android.adapters.SearchAdapter
import quickbeer.android.viewmodels.SearchViewViewModel
import timber.log.Timber

class SearchView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private val searchAdapter = SearchAdapter(context)

    private lateinit var viewModel: SearchViewViewModel

    fun setViewModel(model: SearchViewViewModel) {
        viewModel = model

        initialize()
        closeSearchView()
    }

    fun setMenuItem(menuItem: MenuItem) {
        search_view.setMenuItem(menuItem)
    }

    fun isSearchViewOpen(): Boolean {
        return search_view?.isSearchOpen == true
    }

    fun closeSearchView() {
        search_view.closeSearch()
    }

    fun updateQueryList(queries: List<String>) {
        searchAdapter.updateSourceList(queries)
    }

    private fun initialize() {
        search_view_overlay.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP && search_view.isSearchOpen) {
                search_view.closeSearch()
            }
            true
        }

        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Timber.d("onQueryTextSubmit($query)")
                viewModel.submitted = true
                if (updateQueryText(query)) {
                    search_view.closeSearch()
                }
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                Timber.v("onQueryTextChange($query)")
                if (!viewModel.submitted && viewModel.liveFilteringEnabled()) {
                    updateQueryText(query)
                }
                return true
            }

            private fun updateQueryText(query: String): Boolean {
                return if (query.length >= viewModel.minimumSearchLength()) {
                    viewModel.query = query
                    true
                } else {
                    showTooShortSearchError()
                    false
                }
            }
        })

        search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewAboutToClose() {}

            override fun onSearchViewAboutToShow() {
                Timber.d("onSearchViewAboutToShow")

                viewModel.submitted = false

                search_view.setHint(viewModel.searchHint)
                search_view.setQuery(viewModel.query, false)

                if (viewModel.contentOverlayEnabled()) {
                    val fadeIn = AlphaAnimation(0.0f, 0.5f)
                    fadeIn.duration = AnimationUtil.ANIMATION_DURATION_MEDIUM.toLong()
                    fadeIn.fillAfter = true

                    search_view_overlay.visibility = View.VISIBLE
                    search_view_overlay.startAnimation(fadeIn)
                }
            }

            override fun onSearchViewShown() {
                Timber.d("onSearchViewShown")

                search_view.setAdapter(searchAdapter)
            }

            override fun onSearchViewClosed() {
                Timber.d("onSearchViewClosed")

                search_view.setAdapter(null)
                search_view_overlay.clearAnimation()
                search_view_overlay.visibility = View.GONE
            }
        })

        search_view.setOnItemClickListener { parent, view, position, id ->
            search_view.closeSearch()
            viewModel.query = searchAdapter.getItem(position)
        }
    }

    fun showTooShortSearchError() {
        Toast.makeText(context, R.string.search_too_short, Toast.LENGTH_SHORT).show()
    }
}
