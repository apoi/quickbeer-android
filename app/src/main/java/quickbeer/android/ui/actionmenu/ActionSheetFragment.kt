package quickbeer.android.ui.actionmenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import quickbeer.android.R
import quickbeer.android.ui.base.BaseBottomSheetFragment

class ActionSheetFragment : BaseBottomSheetFragment() {

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

                ActionSheetFragmentComposable(title = "Actions", items = items, ::selectAction)
            }
        }
    }

    private fun selectAction(action: Int) {
        dismiss()
    }
}
