package quickbeer.android.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.BuildConfig
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.ProfileLegalFragmentBinding
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

class ProfileLegalFragment : BaseFragment(R.layout.profile_legal_fragment) {

    private val binding by viewBinding(ProfileLegalFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.linkOpenSource.setOnClickListener { openLink(Constants.OPEN_SOURCE_LICENSES) }
        binding.linkGraphicsAssets.setOnClickListener { openLink(Constants.GRAPHICS_LICENSES) }
        binding.linkLicense.setOnClickListener { openLink(Constants.QUICKBEER_LICENSE) }
        binding.linkPrivacy.setOnClickListener { openLink(Constants.PRIVACY_POLICY) }
        binding.linkSource.setOnClickListener { openLink(Constants.QUICKBEER_GITHUB) }
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
