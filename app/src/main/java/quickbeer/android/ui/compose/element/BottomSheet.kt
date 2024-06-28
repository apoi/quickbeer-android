package quickbeer.android.ui.compose.element

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.util.ktx.topElevation

@Composable
fun BottomSheet(
    scrollState: ScrollState,
    content: @Composable ColumnScope.() -> Unit,
    stickyContent: @Composable ColumnScope.() -> Unit
) {
    // Just bottom sheet visuals, due to the Compose bottom sheet elements not
    // playing well together with the containing BottomSheetDialogFragment.
    Surface(
        modifier = Modifier
            .nestedScroll(rememberNestedScrollInteropConnection()),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        color = Colors.cardBackgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            TopAppBar(
                backgroundColor = Colors.cardBackgroundColor,
                contentColor = Colors.cardBackgroundColor,
                elevation = scrollState.topElevation
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 22.dp)
                            .align(Alignment.Center)
                            .background(Colors.bottomSheetDragHandleColor)
                            .clip(MaterialTheme.shapes.large)
                    ) {
                        Box(Modifier.size(width = 32.dp, height = 4.dp))
                    }
                }
            }

            content()

            stickyContent()
        }
    }
}