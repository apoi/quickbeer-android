package quickbeer.android.feature.barcode.utils

import com.google.mlkit.vision.barcode.common.Barcode

@Suppress("MagicNumber")
object BarcodeValidator {

    private val PATTERN = "^(\\d{8}|\\d{12,14})$".toRegex()

    private val BARCODE_FORMATS = listOf(
        Barcode.FORMAT_EAN_8,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_UPC_A,
        Barcode.FORMAT_UPC_E
    )

    fun isValidBarcode(barcode: Barcode): Boolean {
        return BARCODE_FORMATS.contains(barcode.format) &&
            isValidBarcode(barcode.rawValue)
    }

    fun isValidBarcode(barcode: String?): Boolean {
        if (barcode?.matches(PATTERN) != true) return false

        val code = barcode.padStart(14, '0')
        val sum = code.foldIndexed(0) { index, sum, c ->
            sum + (c - '0') * (if (index % 2 == 0) 3 else 1)
        }

        return sum % 10 == 0
    }
}
