package quickbeer.android.feature.beerrating.model

sealed class BeerRatingViewEvent {

    data object SaveDraftSuccess : BeerRatingViewEvent()

    data object PublishSuccess : BeerRatingViewEvent()

    data class PublishError(val cause: Throwable) : BeerRatingViewEvent()
}
