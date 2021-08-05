package quickbeer.android.feature.login

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.databinding.LoginDialogFragmentBinding
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.observe
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class LoginDialog : DialogFragment(R.layout.login_dialog_fragment) {

    @Inject
    lateinit var toastProvider: ToastProvider

    private val binding by viewBinding(LoginDialogFragmentBinding::bind)
    private val viewModel by viewModels<LoginViewModel>()

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
            binding.button.isLoading = state is State.Loading

            when (state) {
                is State.Initial, is State.Loading -> Unit
                is State.Error -> showError(state.cause)
                is State.Success -> dismiss()
                is State.Empty -> showError(LoginError.UnknownError)
            }
        }
    }

    private fun showError(error: Throwable) {
        when (error) {
            is LoginError.InvalidCredentials -> toastProvider.showToast(R.string.login_failed)
            else -> toastProvider.showToast(R.string.login_error)
        }
    }
}
