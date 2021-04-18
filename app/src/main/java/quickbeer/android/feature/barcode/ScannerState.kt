package quickbeer.android.feature.barcode

import com.google.mlkit.vision.barcode.Barcode
import quickbeer.android.domain.beer.Beer

sealed class ScannerState {
    object NotStarted : ScannerState()
    object Detecting : ScannerState()
    data class Detected(val barcode: Barcode) : ScannerState()
    data class Confirming(val barcode: Barcode) : ScannerState()
    data class Searching(val barcode: Barcode) : ScannerState()
    data class Found(val barcode: Barcode, val beers: List<Beer>) : ScannerState()
    data class NotFound(val barcode: Barcode) : ScannerState()
    data class Error(val barcode: Barcode) : ScannerState()
}
