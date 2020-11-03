package quickbeer.android.feature.topbeers

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import quickbeer.android.R
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

class TopBeersFragment : BaseFragment(R.layout.beer_list_fragment) {

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val photoAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    protected val viewModel: TopBeersViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchMenuItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchMenuItem.actionView as SearchView
        setupSearchView(searchView, searchMenuItem)

        binding.recyclerView.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
        }
    }

    private fun setupSearchView(searchView: SearchView, searchMenuItem: MenuItem) {
        searchView.suggestionsAdapter = viewModel.getSearchAdapter(requireActivity())

        searchMenuItem.setOnActionExpandListener(
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    // Set styles for expanded state here
                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.white, null))
                    binding.toolbar.collapseIcon?.setTint(resources.getColor(R.color.icon_dark, null))
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    // Set styles for collapsed state here
                    binding.toolbar.setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
                    binding.toolbar.collapseIcon?.setTint(resources.getColor(R.color.icon_light, null))
                    return true
                }
            })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {
                viewModel.onSearchChanged(query)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.onSearchSubmit(query)
                return true
            }
        })

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                viewModel.onSuggestionClicked(position)
                return true
            }
        })
    }

    override fun observeViewState() {
        observe(viewModel.viewState, photoAdapter::setItems)
    }
}
