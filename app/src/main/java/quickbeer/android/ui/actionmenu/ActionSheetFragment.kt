package quickbeer.android.ui.actionmenu

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.navArgs
import com.google.android.material.R as materialR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import quickbeer.android.R
import quickbeer.android.ui.base.BaseBottomSheetFragment
import quickbeer.android.util.ktx.setNavigationResult

class ActionSheetFragment : BaseBottomSheetFragment() {

    private val args by navArgs<ActionSheetFragmentArgs>()

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
                ActionSheetFragmentComposable(
                    title = getString(R.string.actions_title),
                    items = args.actions.toList(),
                    ::selectAction
                )
            }
        }
    }

    private fun selectAction(action: Action) {
        setNavigationResult(ACTION_RESULT, action)
        dismiss()
    }

    companion object {
        const val ACTION_RESULT = "ActionResult"
    }
}
