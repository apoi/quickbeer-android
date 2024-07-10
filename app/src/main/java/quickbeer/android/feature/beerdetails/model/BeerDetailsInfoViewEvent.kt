package quickbeer.android.feature.beerdetails.model

sealed class BeerDetailsInfoViewEvent {

    data class ShowMessage(val message: String) : BeerDetailsInfoViewEvent()
}
