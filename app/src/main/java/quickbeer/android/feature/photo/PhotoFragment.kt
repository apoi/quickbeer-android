package quickbeer.android.feature.photo

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import coil.load
import quickbeer.android.R
import quickbeer.android.databinding.PhotoFragmentBinding
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

class PhotoFragment : BaseFragment(R.layout.photo_fragment) {

    private val binding by viewBinding(PhotoFragmentBinding::bind)
    private val args: PhotoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photoView.load(args.uri)
    }
}
