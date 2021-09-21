package quickbeer.android.feature.profile

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.databinding.TextContentFragmentBinding
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.ktx.removeSingleLineBreaks
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class LicenseFragment : BaseFragment(R.layout.text_content_fragment) {

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val binding by viewBinding(TextContentFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.toolbar.title = getString(R.string.quickbeer_license_title)

        binding.content.text = resourceProvider
            .getRaw(R.raw.license)
            .removeSingleLineBreaks()
    }
}
