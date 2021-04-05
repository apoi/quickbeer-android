package quickbeer.android.feature.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.DiscoverTabTitleBinding
import quickbeer.android.databinding.SearchFragmentBinding
import quickbeer.android.ui.listener.OnTabSelected
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.view.SearchView
import quickbeer.android.util.ktx.hideKeyboard
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding
import timber.log.Timber

class SearchFragment : SearchBarFragment(R.layout.search_fragment) {

    private val binding by viewBinding(SearchFragmentBinding::bind)
    private val searchViewModel by sharedViewModel<SearchViewModel>()

    override val searchHint = R.string.search_hint
    override fun rootLayout() = binding.layout
    override fun topInsetView() = binding.contentLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel.onSearchQueryChanged("")
        searchViewModel.onSearchTypeChanged(SearchViewModel.SearchType.BEER)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.navigationMode = SearchView.NavigationMode.BACK
    }

    @Suppress("MagicNumber")
    override fun onInitialViewCreated() {
        // Delay for search field opening animation
        requireView().postDelayed({ binding.searchView.openSearchView() }, 150)

        // Animated background reveal after search is opened
        binding.searchBackground.animate()
            .scaleY(1.0f)
            .setDuration(200)
            .setStartDelay(350)
            .start()
    }

    override fun onRestoreView() {
        binding.searchView.openSearchView()
        binding.searchBackground.scaleY = 1.0f
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewPager.adapter = SearchPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.addOnTabSelectedListener(
            OnTabSelected { index ->
                val searchType = SearchViewModel.SearchType.fromValue(index)
                searchViewModel.onSearchTypeChanged(searchType)
                binding.searchView.hideKeyboard()
            }
        )

        // Set custom tab layouts to get progress indicators
        (0 until binding.tabLayout.tabCount)
            .forEach { index ->
                val searchType = SearchViewModel.SearchType.fromValue(index)
                val tabBinding = DiscoverTabTitleBinding.inflate(LayoutInflater.from(context))
                tabBinding.title.text = when (searchType) {
                    SearchViewModel.SearchType.BEER -> getString(R.string.search_tab_beers)
                    SearchViewModel.SearchType.BREWER -> getString(R.string.search_tab_brewers)
                    SearchViewModel.SearchType.STYLE -> getString(R.string.search_tab_styles)
                    SearchViewModel.SearchType.COUNTRY -> getString(R.string.search_tab_countries)
                }
                binding.tabLayout.getTabAt(index)?.customView = tabBinding.layout
            }
    }

    override fun observeViewState() {
        observe(searchViewModel.beerResults) { state ->
            updateSearchTabProgress(0, state is State.Loading)
        }

        observe(searchViewModel.brewerResults) { state ->
            updateSearchTabProgress(1, state is State.Loading)
        }

        observe(searchViewModel.styleResults) { state ->
            updateSearchTabProgress(2, state is State.Loading)
        }

        observe(searchViewModel.countryResults) { state ->
            updateSearchTabProgress(3, state is State.Loading)
        }
    }

    private fun updateSearchTabProgress(tab: Int, inProgress: Boolean) {
        binding.tabLayout.getTabAt(tab)
            ?.customView
            ?.findViewById<CircularProgressIndicator>(R.id.progress)
            ?.isVisible = inProgress
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun onSearchQueryChanged(query: String) {
        searchViewModel.onSearchQueryChanged(query)
    }

    override fun onSearchQuerySubmit(query: String) {
        searchViewModel.onSearchQueryChanged(query)
    }

    override fun onSearchBarcode() {
        Timber.w("DO BARCODE")
    }
}
