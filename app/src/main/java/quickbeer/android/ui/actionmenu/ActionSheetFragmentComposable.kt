package quickbeer.android.ui.actionmenu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        scrollState = scrollState,
        content = { ActionList(title, items, onItemSelected) }
    )
}

@Composable
fun ActionList(title: String, items: List<Pair<Int, Int>>, onItemSelected: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        itemsIndexed(items) { index, (key, value) ->
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemSelected(key) },
                style = TextStyles.textM,
                color = Colors.textLight,
                text = stringResource(value)
            )
            if (index < items.lastIndex) {
                Divider(color = Colors.iconLight)
            }
        }
    }
}
