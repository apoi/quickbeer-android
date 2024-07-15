package quickbeer.android.feature.recent

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.RecentFragmentBinding
import quickbeer.android.navigation.NavAnim
import quickbeer.android.ui.base.Resetable
import quickbeer.android.ui.search.SearchBarFragment
import quickbeer.android.ui.view.SearchView
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class RecentFragment : SearchBarFragment(R.layout.recent_fragment), Resetable {

    private val binding by viewBinding(
        bind = RecentFragmentBinding::bind,
        destroyCallback = {
            mediator?.detach()
            mediator = null
            it.viewPager.adapter = null
        }
    )

    override val searchHint = R.string.search_hint

    override fun topInsetView() = binding.layout

    private var mediator: TabLayoutMediator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = RecentItemsPagerAdapter(this)

        mediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.recent_beers)
                else -> getString(R.string.recent_brewers)
            }
        }.also(TabLayoutMediator::attach)
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
