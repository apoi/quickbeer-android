package quickbeer.android.ui.compose.element

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import quickbeer.android.ui.compose.style.ButtonStyles.ButtonStyle

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: ButtonStyle,
    text: String
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = style.colors
    ) {
        Text(
            color = style.textColor,
            style = style.textStyle,
            text = text
        )
    }
}
