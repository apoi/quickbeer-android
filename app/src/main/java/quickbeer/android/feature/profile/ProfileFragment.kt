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
        binding.linkLegal.setOnClickListener { navigate(ProfileFragmentDirections.toLegal()) }

        binding.aboutVersion.text =
            getString(R.string.about_version).format(BuildConfig.VERSION_NAME)
    }

    override fun observeViewState() {
        observe(viewModel.userState) { state ->
            when (state) {
                is State.Initial -> Unit
                is State.Loading -> setStateContent(state.value)
                is State.Success -> setStateContent(state.value)
                is State.Empty, is State.Error -> setStateEmpty()
            }
        }

        observe(viewModel.ratingCountState) { state ->
            when (state) {
                is State.Initial -> setRateCount(null)
                is State.Loading -> setRateCount(null)
                is State.Success -> setRateCount(state.value)
                is State.Empty, is State.Error -> setRateCount(0)
            }
        }

        observe(viewModel.tickCountState) { state ->
            when (state) {
                is State.Initial -> setTickCount(null)
                is State.Loading -> setTickCount(null)
                is State.Success -> setTickCount(state.value)
                is State.Empty, is State.Error -> setTickCount(0)
            }
        }
    }

    private fun setProfileButtons(hasUser: Boolean) {
        binding.profileRatings.isVisible = hasUser
        binding.profileTicks.isVisible = hasUser
        binding.profileLogout.isVisible = hasUser
        binding.profileLogin.isVisible = !hasUser
    }

    private fun setStateContent(user: User?) {
        setProfileButtons(true)
        setProfile(user)
    }

    private fun setProfile(user: User?) {
        binding.profileUsername.text = user?.username
            ?: getString(R.string.profile_loading_profile)
    }

    private fun setRateCount(count: Int?) {
        binding.profileRatings.label.text =
            getString(R.string.profile_ratings)
                .format(count ?: getString(R.string.profile_loading_ratings))

        if (count != null) {
            binding.profileRatings.setOnClickListener {
                navigate(ProfileFragmentDirections.toRatingsList())
            }
        } else {
            binding.profileRatings.setOnClickListener(null)
        }
    }

    private fun setTickCount(count: Int?) {
        binding.profileTicks.label.text =
            getString(R.string.profile_ticks)
                .format(count ?: getString(R.string.profile_loading_ratings))

        if (count != null) {
            binding.profileTicks.setOnClickListener {
                navigate(ProfileFragmentDirections.toTicksList())
            }
        } else {
            binding.profileTicks.setOnClickListener(null)
        }
    }

    private fun setStateEmpty() {
        setProfileButtons(false)
        binding.profileUsername.text = getString(R.string.profile_not_logged)
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        requireContext().startActivity(intent)
    }
}
