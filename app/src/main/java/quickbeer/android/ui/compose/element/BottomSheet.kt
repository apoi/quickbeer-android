package quickbeer.android.ui.compose.element

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.style.Colors

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(
    content: @Composable ColumnScope.() -> Unit,
    stickyContent: @Composable ColumnScope.() -> Unit
) {
    // Just bottom sheet visuals, due to the Compose bottom sheet elements not
    // playing well together with the containing BottomSheetDialogFragment.
    Surface(
        modifier = Modifier
            .windowInsetsPadding(BottomSheetDefaults.windowInsets)
            .nestedScroll(rememberNestedScrollInteropConnection()),
        shape = BottomSheetDefaults.ExpandedShape,
        color = Colors.cardBackgroundColor,
        tonalElevation = BottomSheetDefaults.Elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())

            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 22.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Colors.bottomSheetDragHandleColor)
                        .clip(MaterialTheme.shapes.extraLarge)
                ) {
                    Box(Modifier.size(width = 32.dp, height = 4.dp))
                }
            }

            content()

            stickyContent()
        }
    }
}
