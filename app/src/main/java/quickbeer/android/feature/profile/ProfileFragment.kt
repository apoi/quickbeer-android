package quickbeer.android.feature.profile

import android.os.Bundle
import android.view.View
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
    }
}
