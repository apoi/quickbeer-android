package quickbeer.android.ui.compose.style

import androidx.compose.material.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Stable
object ButtonStyles {

    @Composable
    fun primary() = ButtonStyle(
        colors = Colors.primaryButtonColors(),
        textColor = Colors.textDark,
        textStyle = TextStyles.textSHeavy
    )

    @Composable
    fun secondary() = ButtonStyle(
        colors = Colors.secondaryButtonColors(),
        textColor = Colors.textLight,
        textStyle = TextStyles.textSHeavy
    )

    class ButtonStyle(
        val colors: ButtonColors,
        val textColor: Color,
        val textStyle: TextStyle
    )
}
