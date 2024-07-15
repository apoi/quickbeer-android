package quickbeer.android.feature.discover

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.databinding.DiscoverFragmentBinding
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class DiscoverFragment : BaseFragment(R.layout.discover_fragment) {

    private val binding by viewBinding(DiscoverFragmentBinding::bind)
    private val viewModel by viewModels<DiscoverViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.discoverTopBeers.setOnClickListener {
            navigate(DiscoverFragmentDirections.toTopBeers())
        }

        binding.discoverCountries.setOnClickListener {
            navigate(DiscoverFragmentDirections.toCountryList())
        }

        binding.discoverStyles.setOnClickListener {
        }
    }

    override fun observeViewState() {
    }
}
