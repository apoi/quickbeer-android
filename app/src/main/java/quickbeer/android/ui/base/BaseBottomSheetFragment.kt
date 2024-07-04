package quickbeer.android.ui.base

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import quickbeer.android.R

abstract class BaseBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_BottomSheet)
    }
}
