package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.DiscoverFragmentBinding
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toCountryList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toFeed
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toStyleList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toTopBeers
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_FRIENDS
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_GLOBAL
import quickbeer.android.feature.feed.FeedFragment.Companion.MODE_LOCAL
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class DiscoverFragment : BaseFragment(R.layout.discover_fragment) {

    private val binding by viewBinding(DiscoverFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.discoverTopBeers.setOnClickListener { navigate(toTopBeers()) }

        binding.discoverCountries.setOnClickListener { navigate(toCountryList()) }
        binding.discoverStyles.setOnClickListener { navigate(toStyleList()) }

        binding.friendsFeed.setOnClickListener { navigate(toFeed(mode = MODE_FRIENDS)) }
        binding.localFeed.setOnClickListener { navigate(toFeed(mode = MODE_LOCAL)) }
        binding.globalFeed.setOnClickListener { navigate(toFeed(mode = MODE_GLOBAL)) }
    }

    override fun observeViewState() {
    }
}
