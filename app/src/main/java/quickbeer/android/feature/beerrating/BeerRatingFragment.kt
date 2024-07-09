package quickbeer.android.feature.beerrating

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.google.android.material.R as materialR
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.data.state.State
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.PublishError
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.PublishSuccess
import quickbeer.android.feature.beerrating.model.BeerRatingViewEvent.SaveDraftSuccess
import quickbeer.android.ui.base.BaseBottomSheetFragment
import quickbeer.android.util.ToastProvider
import quickbeer.android.util.ktx.observe
import timber.log.Timber

@AndroidEntryPoint
class BeerRatingFragment : BaseBottomSheetFragment() {

    @Inject
    lateinit var toastProvider: ToastProvider

    private val viewModel by viewModels<BeerRatingViewModel>()

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
                when (val state = viewModel.viewState.collectAsState().value) {
                    is State.Initial, is State.Loading -> LoadingSheetComposable()
                    is State.Success -> RatingSheetComposable(state.value, viewModel)
                    is State.Empty, is State.Error -> error("Invalid view state")
                }
            }
        }
    }

    override fun observeViewState() {
        observe(viewModel.events) { event ->
            when (event) {
                is SaveDraftSuccess -> closeWithMessage(R.string.rating_draft_success)
                is PublishSuccess -> closeWithMessage(R.string.rating_publish_success)
                is PublishError -> showError(event.cause)
            }
        }
    }

    private fun closeWithMessage(@StringRes message: Int) {
        toastProvider.showToast(getString(message))
        dismiss()
    }

    private fun showError(cause: Throwable) {
        Timber.e(cause)
        toastProvider.showToast(getString(R.string.rating_publish_error))
    }
}
