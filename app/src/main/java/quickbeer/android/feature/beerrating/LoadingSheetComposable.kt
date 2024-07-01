package quickbeer.android.feature.beerrating

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import quickbeer.android.ui.compose.element.BottomSheet
import quickbeer.android.ui.compose.style.Colors

@Composable
fun LoadingSheetComposable() {
    val scrollState = rememberScrollState()

    BottomSheet(
        scrollState = scrollState,
        content = { LoadingIndicator() }
    )
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Colors.highlight)
    }
}
