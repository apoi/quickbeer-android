package quickbeer.android.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.BuildConfig
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ProfileFragmentBinding
import quickbeer.android.domain.user.User
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.ui.view.GroupItem
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class ProfileFragment : MainFragment(R.layout.profile_fragment) {

    private val binding by viewBinding(ProfileFragmentBinding::bind)
    private val viewModel by viewModels<ProfileViewModel>()

    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            navigate(ProfileFragmentDirections.toLogin())
        }

        binding.aboutVersion.text =
            getString(R.string.about_version).format(BuildConfig.VERSION_NAME)

        binding.linkPlayStore.setOnClickListener { openLink(Constants.PLAY_STORE) }
        binding.linkPrivacy.setOnClickListener { openLink(Constants.PRIVACY_POLICY) }
        binding.linkSource.setOnClickListener { openLink(Constants.QUICKBEER_GITHUB) }
        binding.linkLicense.setOnClickListener { openLink(Constants.QUICKBEER_LICENSE) }
        binding.linkOpenSource.setOnClickListener { openLink(Constants.OPEN_SOURCE_LICENSES) }
        binding.linkGraphicsAssets.setOnClickListener { openLink(Constants.GRAPHICS_LICENSES) }
    }

    override fun observeViewState() {
        observe(viewModel.userState) { state ->
            when (state) {
                is State.Initial, is State.Loading -> setLoading()
                is State.Success -> setUser(state.value)
                is State.Empty, is State.Error -> setEmpty()
            }
        }
    }

    private fun setLoading() {
        binding.profileUsername.text = "Loading..."
    }

    private fun setUser(user: User) {
        binding.profileItem.position = GroupItem.Position.FIRST
        binding.profileUsername.text = user.username

        binding.profileReviews.apply {
            label.text = getString(R.string.profile_reviews).format(user.rateCount)
            isVisible = true
        }

        binding.profileTicks.apply {
            label.text = getString(R.string.profile_ticks).format(user.tickCount)
            isVisible = true
        }
    }

    private fun setEmpty() {
        binding.profileUsername.text = "Not logged in"
        binding.loginButton.isVisible = true
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
