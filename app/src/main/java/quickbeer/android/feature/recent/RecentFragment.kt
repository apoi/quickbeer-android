package quickbeer.android.feature.recent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.RecentFragmentBinding
import quickbeer.android.databinding.RecentTabTitleBinding
import quickbeer.android.navigation.NavAnim
import quickbeer.android.ui.base.Resetable
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.view.SearchView
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class RecentFragment : SearchBarFragment(R.layout.recent_fragment), Resetable {

    private val binding by viewBinding(
        bind = RecentFragmentBinding::bind,
        destroyCallback = { it.viewPager.adapter = null }
    )

    override val searchHint = R.string.search_hint

    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = RecentItemsPagerAdapter(childFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // Set custom tab layouts to get progress indicators
        (0.until(binding.tabLayout.tabCount))
            .forEach { index ->
                val tabBinding = RecentTabTitleBinding.inflate(LayoutInflater.from(context))
                tabBinding.title.text = when (index) {
                    0 -> getString(R.string.search_tab_beers)
                    1 -> getString(R.string.search_tab_brewers)
                    else -> getString(R.string.search_tab_places)
                }
                binding.tabLayout.getTabAt(index)?.customView = tabBinding.layout
            }
    }

    override fun searchView(): SearchView {
        return binding.searchView
    }

    override fun onSearchFocusChanged(hasFocus: Boolean) {
        super.onSearchFocusChanged(hasFocus)

        if (hasFocus) {
            navigate(RecentFragmentDirections.toSearch(), NavAnim.NONE)
        }
    }

    override fun onReset() {
        binding.viewPager.setCurrentItem(0, true)
        childFragmentManager.fragments.filterIsInstance<Resetable>().forEach(Resetable::onReset)
    }
}
