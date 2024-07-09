package quickbeer.android.ui.compose.element

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import quickbeer.android.ui.compose.style.ButtonStyles.ButtonStyle
import quickbeer.android.ui.compose.style.Dimens

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
        enabled = enabled && !loading,
        colors = style.colors
    ) {
        if (!loading) {
            Text(
                color = style.textColor,
                style = style.textStyle,
                text = text
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.icon),
                strokeWidth = 2.dp,
                color = style.loadingIndicatorColor
            )
        }
    }
}
