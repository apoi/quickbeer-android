package quickbeer.android.feature.search

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quickbeer.android.R
import quickbeer.android.databinding.SearchFragmentBinding
import quickbeer.android.ui.search.SearchActionsHandler
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.searchview.widget.SearchView
import quickbeer.android.ui.searchview.widget.SearchView.NavigationMode
import quickbeer.android.util.ktx.viewBinding

class SearchFragment : SearchBarFragment(R.layout.search_fragment) {

    private val args: SearchFragmentArgs by navArgs()
    private val binding by viewBinding(SearchFragmentBinding::bind)
    private val viewModel by sharedViewModel<SearchViewModel>()

    override val searchHint = R.string.search_hint
    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.contentLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // viewModel.search(args.query)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchView.apply {
            query = args.query
            navigationMode = NavigationMode.BACK
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = SearchPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun searchActions(): SearchActionsHandler {
        return viewModel
    }

    override fun onSearchQuerySubmit(query: String) {
        // viewModel.search(query)
    }
}
