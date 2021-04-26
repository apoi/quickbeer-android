package quickbeer.android.feature.login

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.LoginDialogFragmentBinding
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

class LoginDialog : DialogFragment(R.layout.login_dialog_fragment) {

    private val binding by viewBinding(LoginDialogFragmentBinding::bind)
    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            viewModel.login(username, password)
        }

        observeViewState()
    }

    private fun observeViewState() {
        observe(viewModel.loginState) { state ->
            when (state) {
                is State.Loading -> binding.button.isLoading = true
                is State.Empty -> binding.button.isLoading = false
                is State.Error -> binding.button.isLoading = false
                is State.Success -> binding.button.isLoading = false
            }
        }
    }
}
