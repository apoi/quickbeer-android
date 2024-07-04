package quickbeer.android.ui.compose.element

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import quickbeer.android.ui.compose.style.ButtonStyles.ButtonStyle

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    style: ButtonStyle,
    text: String
) {
    androidx.compose.material.Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = style.colors
    ) {
        if (!loading) {
            Text(
                color = style.textColor,
                style = style.textStyle,
                text = text
            )
        } else {
            CircularProgressIndicator()
        }
    }
}
