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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.Dimens
import quickbeer.android.ui.compose.style.TextStyles
import quickbeer.android.util.ktx.topElevation

@Composable
fun BottomSheet(
    modifier: Modifier,
    scrollState: ScrollState,
    title: String? = null,
    showHandle: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = {},
    stickyContent: @Composable ColumnScope.() -> Unit = {}
) {
    val sheetScrollState = rememberScrollState()

    // Just bottom sheet visuals, due to the Compose bottom sheet elements not
    // playing well together with the containing BottomSheetDialogFragment.
    Surface(
        color = Colors.cardBackgroundColor,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            // Surface to provide elevation, as elevation directly on TopAppBar
            // doesn't work if it has verticalScroll() set
            Surface(
                elevation = scrollState.topElevation
            ) {
                TopAppBar(
                    modifier = Modifier
                        // Allows bottom sheet dragging from the top bar
                        .nestedScroll(rememberNestedScrollInteropConnection())
                        .verticalScroll(sheetScrollState),
                    backgroundColor = Colors.cardBackgroundColor,
                    contentColor = Colors.cardBackgroundColor
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        if (title != null) {
                            Text(
                                modifier = Modifier
                                    .padding(Dimens.spacingXL)
                                    .align(Alignment.CenterStart),
                                text = title,
                                style = TextStyles.textMHeavy,
                                color = Colors.textLight
                            )
                        } else if (showHandle) {
                            Box(
                                modifier = Modifier
                                    .size(width = 32.dp, height = 4.dp)
                                    .align(Alignment.Center)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Colors.bottomSheetDragHandleColor)
                            )
                        }
                    }
                }
            }

            content()

            stickyContent()
        }
    }
}
