package quickbeer.android.feature.more

import android.os.Bundle
import android.view.View
import quickbeer.android.R
import quickbeer.android.databinding.MoreFragmentBinding
import quickbeer.android.ui.base.MainFragment
import quickbeer.android.util.ktx.viewBinding

class MoreFragment : MainFragment(R.layout.more_fragment) {

    private val binding by viewBinding(MoreFragmentBinding::bind)

    override fun topInsetView() = binding.layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            navigate(MoreFragmentDirections.toLogin())
        }
    }
}
