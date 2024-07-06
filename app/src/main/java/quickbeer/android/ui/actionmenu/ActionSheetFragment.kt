package quickbeer.android.ui.actionmenu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.R as materialR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import quickbeer.android.R
import quickbeer.android.ui.base.BaseBottomSheetFragment

class ActionSheetFragment : BaseBottomSheetFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<View>(materialR.id.design_bottom_sheet) as? FrameLayout
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT

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
                val items = listOf(
                    Pair(1, R.string.edit_draft),
                    Pair(2, R.string.add_rating),
                    Pair(2, R.string.add_rating),
                    Pair(2, R.string.add_rating),
                    Pair(2, R.string.add_rating)
                )

                ActionSheetFragmentComposable(
                    title = getString(R.string.actions_title),
                    items = items,
                    ::selectAction
                )
            }
        }
    }

    private fun selectAction(action: Int) {
        dismiss()
    }
}
