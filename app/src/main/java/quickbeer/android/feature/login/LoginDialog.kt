package quickbeer.android.feature.login

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.DialogFragment
import quickbeer.android.R
import quickbeer.android.databinding.LoginDialogFragmentBinding
import quickbeer.android.util.ktx.viewBinding

class LoginDialog : DialogFragment(R.layout.login_dialog_fragment) {

    private val binding by viewBinding(LoginDialogFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button.apply {
            setOnClickListener { isLoading = !isLoading }
        }
    }
}
