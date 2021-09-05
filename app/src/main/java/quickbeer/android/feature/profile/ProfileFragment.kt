package quickbeer.android.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import quickbeer.android.BuildConfig
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.ProfileFragmentBinding
import quickbeer.android.domain.user.User
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class ProfileFragment : BaseFragment(R.layout.profile_fragment) {

    private val binding by viewBinding(ProfileFragmentBinding::bind)
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileLogin.setOnClickListener {
            navigate(ProfileFragmentDirections.toLogin())
        }

        binding.profileLogout.setOnClickListener {
            lifecycleScope.launch { viewModel.logout() }
        }

        binding.linkPlayStore.setOnClickListener { openLink(Constants.PLAY_STORE) }
        binding.linkPrivacy.setOnClickListener { openLink(Constants.PRIVACY_POLICY) }
        binding.linkSource.setOnClickListener { openLink(Constants.QUICKBEER_GITHUB) }
        binding.linkLicense.setOnClickListener { openLink(Constants.QUICKBEER_LICENSE) }
        binding.linkOpenSource.setOnClickListener { openLink(Constants.OPEN_SOURCE_LICENSES) }
        binding.linkGraphicsAssets.setOnClickListener { openLink(Constants.GRAPHICS_LICENSES) }

        binding.aboutVersion.text =
            getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
    }

    override fun observeViewState() {
        observe(viewModel.hasUser, ::setProfileButtons)
        observe(viewModel.tickCount, ::setStateTickCount)

        observe(viewModel.userState) { state ->
            when (state) {
                is State.Initial, is State.Loading -> setStateLoading()
                is State.Success -> setStateContent(state.value)
                is State.Empty, is State.Error -> setStateEmpty()
            }
        }
    }

    private fun setProfileButtons(hasUser: Boolean) {
        binding.profileReviews.isVisible = false // Reviews hidden
        binding.profileTicks.isVisible = hasUser
        binding.profileLogout.isVisible = hasUser
        binding.profileLogin.isVisible = !hasUser
    }

    private fun setStateLoading() {
        binding.profileUsername.text = "Loading..."

        binding.profileReviews.label.text =
            getString(R.string.profile_reviews).format("loading...")
    }

    private fun setStateTickCount(state: State<Int>) {
        if (state is State.Success) {
            binding.profileTicks.label.text =
                getString(R.string.profile_ticks).format(state.value)
        } else {
            binding.profileTicks.label.text =
                getString(R.string.profile_ticks).format("loading...")
        }
    }

    private fun setStateContent(user: User) {
        binding.profileUsername.text = user.username

        binding.profileReviews.label.text =
            getString(R.string.profile_reviews).format(user.rateCount)

        binding.profileTicks.setOnClickListener {
            navigate(ProfileFragmentDirections.toTicksList(user.id))
        }
    }

    private fun setStateEmpty() {
        binding.profileUsername.text = "Not logged in"
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
