package quickbeer.android.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import quickbeer.android.BuildConfig
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.ProfileFragmentBinding
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.util.ktx.viewBinding

class ProfileFragment : MainFragment(R.layout.profile_fragment) {

    private val binding by viewBinding(ProfileFragmentBinding::bind)

    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            navigate(ProfileFragmentDirections.toLogin())
        }

        binding.aboutVersion.text =
            getString(R.string.about_version).format(BuildConfig.VERSION_NAME)

        binding.linkPlayStore.setOnClickListener {
            openExternalLink(Constants.PLAY_STORE)
        }

        binding.linkPrivacy.setOnClickListener {
            openExternalLink(Constants.PRIVACY_POLICY)
        }

        binding.linkSource.setOnClickListener {
            openExternalLink(Constants.QUICKBEER_GITHUB)
        }

        binding.linkLicense.setOnClickListener {
            openExternalLink(Constants.QUICKBEER_LICENSE)
        }

        binding.linkOpenSource.setOnClickListener {
            openExternalLink(Constants.OPEN_SOURCE_LICENSES)
        }

        binding.linkGraphicsAssets.setOnClickListener {
            openExternalLink(Constants.GRAPHICS_ASSET_LICENSES)
        }
    }

    private fun openExternalLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
