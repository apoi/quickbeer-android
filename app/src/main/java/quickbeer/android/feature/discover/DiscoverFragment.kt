package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.DiscoverFragmentBinding
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toCountryList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toStyleList
import quickbeer.android.feature.discover.DiscoverFragmentDirections.Companion.toTopBeers
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class DiscoverFragment : BaseFragment(R.layout.discover_fragment) {

    private val binding by viewBinding(DiscoverFragmentBinding::bind)
    private val viewModel by viewModels<DiscoverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.discoverTopBeers.setOnClickListener {
            navigate(toTopBeers())
        }

        binding.discoverCountries.setOnClickListener {
            navigate(toCountryList())
        }

        binding.discoverStyles.setOnClickListener {
            navigate(toStyleList())
        }
    }

    override fun observeViewState() {
    }
}
