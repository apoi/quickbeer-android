package quickbeer.android.feature.beerrating

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.google.android.material.R as materialR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import quickbeer.android.R
import quickbeer.android.ui.base.BaseBottomSheetFragment

@AndroidEntryPoint
class BeerRatingFragment : BaseBottomSheetFragment() {

    private val viewModel by viewModels<BeerRatingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BottomSheet)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(materialR.id.design_bottom_sheet) as? FrameLayout

            val behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.skipCollapsed = true
        }

        return dialog
    }

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
