package quickbeer.android.feature.beerrating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import quickbeer.android.ui.base.BaseBottomSheetFragment

class BeerRatingFragment : BaseBottomSheetFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Creating a ComposeView instance directly
        return ComposeView(requireContext()).apply {
            // Set the layout parameters
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            // Setting the content of the ComposeView
            setContent {
                // Call your Composable function here
                MyComposableContent()
            }
        }
    }
}

@Composable
fun MyComposableContent() {
    // Implement your UI here
    Text(text = "Hello, Compose in Fragment!")
}
