package quickbeer.android.ui.compose.style

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Stable
object TextStyle {

    private object FontFamily {

        val thin = FontFamily(
            Font(
                familyName = DeviceFontFamilyName("sans-serif-thin"),
                weight = FontWeight.Thin
            )
        )

        val heavy = FontFamily(
            Font(
                familyName = DeviceFontFamilyName("sans-serif"),
                weight = FontWeight.Normal
            )
        )

        val smallCaps = FontFamily(
            Font(
                familyName = DeviceFontFamilyName("sans-serif-smallcaps"),
                weight = FontWeight.Normal
            )
        )
    }

    private val baseTextStyle = TextStyle(fontFamily = FontFamily.thin)

    // Basic text styles
    val title = baseTextStyle.copy(fontSize = 24.sp)
    val textM = baseTextStyle.copy(fontSize = 16.sp)
    val textMHeavy = textM.copy(fontFamily = FontFamily.heavy)
    val textS = baseTextStyle.copy(fontSize = 14.sp)
    val textXS = baseTextStyle.copy(fontSize = 12.sp)

    // Specific elements
    val detailsTitle = baseTextStyle.copy(
        fontFamily = FontFamily.smallCaps,
        fontSize = 10.5.sp
    )

    val detailsValue = baseTextStyle.copy(
        fontSize = 17.sp
    )

    val detailsDescription = baseTextStyle.copy(
        fontSize = 14.sp
    )
}
