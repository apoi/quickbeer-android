package quickbeer.android.feature.barcode

import quickbeer.android.domain.beer.Beer

sealed class ScannerState {
    object NotStarted : ScannerState()
    object Detecting : ScannerState()
    data class Detected(val barcode: String) : ScannerState()
    data class Confirming(val barcode: String) : ScannerState()
    data class Searching(val barcode: String) : ScannerState()
    data class Found(val beers: List<Beer>) : ScannerState()
    data class NotFound(val barcode: String) : ScannerState()
    data class Error(val barcode: String) : ScannerState()
}
