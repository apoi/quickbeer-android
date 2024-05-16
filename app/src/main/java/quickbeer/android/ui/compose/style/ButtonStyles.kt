package quickbeer.android.ui.compose.style

import androidx.compose.material3.ButtonColors
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
        textStyle = TextStyles.textM
    )

    @Composable
    fun secondary() = ButtonStyle(
        colors = Colors.secondaryButtonColors(),
        textColor = Colors.textLight,
        textStyle = TextStyles.textM
    )

    class ButtonStyle(
        val colors: ButtonColors,
        val textColor: Color,
        val textStyle: TextStyle
    )
}