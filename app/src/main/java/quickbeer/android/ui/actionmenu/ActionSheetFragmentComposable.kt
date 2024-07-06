package quickbeer.android.ui.actionmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.element.BottomSheet
import quickbeer.android.ui.compose.style.Colors
import quickbeer.android.ui.compose.style.TextStyles

@Composable
fun ActionSheetFragmentComposable(
    title: String,
    items: List<Pair<Int, Int>>,
    onItemSelected: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    BottomSheet(
        modifier = Modifier,
        scrollState = scrollState,
        title = title,
        showHandle = false,
        content = { ActionList(items, onItemSelected) }
    )
}

@Composable
fun ActionList(items: List<Pair<Int, Int>>, onItemSelected: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(items) { index, (key, value) ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onItemSelected(key) }
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterStart),
                    style = TextStyles.textM,
                    color = Colors.textLight,
                    text = stringResource(value)
                )
            }
            if (index < items.lastIndex) {
                Divider(color = Colors.dividerColor)
            }
        }
    }
}
