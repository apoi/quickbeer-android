package quickbeer.android.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.LegalDetailsFragmentBinding
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

class LegalDetailsFragment : BaseFragment(R.layout.legal_details_fragment) {

    private val binding by viewBinding(LegalDetailsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.linkOpenSource.setOnClickListener {
            navigate(LegalDetailsFragmentDirections.toOpenSource())
        }

        binding.linkGraphicsAssets.setOnClickListener { openLink(Constants.GRAPHICS_LICENSES) }

        binding.linkLicense.setOnClickListener {
            navigate(LegalDetailsFragmentDirections.toLicense())
        }

        binding.linkPrivacy.setOnClickListener {
            navigate(LegalDetailsFragmentDirections.toPrivacyPolicy())
        }

        binding.linkSource.setOnClickListener { openLink(Constants.QUICKBEER_GITHUB) }
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
