package quickbeer.android.feature.beerrating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import quickbeer.android.ui.base.BaseBottomSheetFragment

class BeerRatingFragment : BaseBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RatingSheetComposable()
            }
        }
    }
}

