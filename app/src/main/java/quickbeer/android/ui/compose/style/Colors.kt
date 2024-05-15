package quickbeer.android.ui.compose.style

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
object Colors {

    // Defaults
    val black = ColorDef.black
    val white = ColorDef.white
    val highlight = ColorDef.orangeDark

    // Cards
    val cardBackgroundColor = ColorDef.gray80
    val cardFeatureColor = ColorDef.gray30

    // Icons
    val iconLight = ColorDef.white
    val iconDark = Color.Unspecified

    // Text
    val textDark = ColorDef.black
    val textLight = ColorDef.white
    val textHint = ColorDef.gray50
    val textError = ColorDef.redDark

    // Components
    val primaryButton = ColorDef.orangeDark

    @Composable
    fun sliderColors() = SliderDefaults.colors().copy(
        thumbColor = ColorDef.orangeDark,
        activeTrackColor = ColorDef.orangeDark,
        inactiveTrackColor = ColorDef.white10
    )

    @Composable
    fun textFieldColors() = OutlinedTextFieldDefaults.colors().copy(
        focusedTextColor = textLight,
        unfocusedTextColor = textLight,
        cursorColor = textLight,
        focusedIndicatorColor = ColorDef.gray50,
        unfocusedIndicatorColor = ColorDef.gray50,
        focusedLabelColor = ColorDef.gray50,
        unfocusedLabelColor = ColorDef.gray50
    )

    private object ColorDef {

        // Basics
        val black = Color(0xFF000000)
        val white = Color(0xFFFFFFFF)
        val transparent = Color(0x00000000)

        // https://material.io/design/color/the-color-system.html#tools-for-picking-colors
        val gray90 = Color(0xFF212121)
        val gray85 = Color(0xFF303030)
        val gray80 = Color(0xFF424242)
        val gray70 = Color(0xFF616161)
        val gray50 = Color(0xFF9E9E9E)
        val gray40 = Color(0xFFBDBDBD)
        val gray30 = Color(0xFFE0E0E0)

        // Partial colors
        val white10 = Color(0x1AFFFFFF)
        val gray8510 = Color(0xB3303030)

        // Not that many other colors
        val orange = Color(0xFFF39815)
        val orangeDark = Color(0xFFC17D1C)
        val redDark = Color(0xFFAA3232)

        // Search
        val searchShadow = Color(0x99000000)

        // Barcode
        val reticleRipple = Color(0x9AFFFFFF)
        val barcodeReticleBackground = Color(0x99000000)
        val barcodeReticleStroke = Color(0x40000000)
        val barcodeFieldValue = Color(0xFF797979)
        val barcodeFieldBoxStroke = Color(0xFF757575)
    }
}
