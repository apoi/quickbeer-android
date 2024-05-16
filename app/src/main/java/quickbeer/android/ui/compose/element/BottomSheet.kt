package quickbeer.android.ui.compose.element

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import quickbeer.android.ui.compose.style.Colors

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheet(content: @Composable ColumnScope.() -> Unit) {
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded)
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContainerColor = Colors.cardBackgroundColor,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(color = Colors.bottomSheetDragHandleColor)
        },
        sheetContent = content
    ) {}
}
