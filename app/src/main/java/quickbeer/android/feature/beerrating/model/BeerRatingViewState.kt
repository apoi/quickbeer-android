package quickbeer.android.feature.beerrating.model

import quickbeer.android.domain.rating.Rating

data class BeerRatingViewState(
    val rating: Rating,
    val isPublishing: Boolean,
    val isSavingDraft: Boolean
) {

    val isEnabled = !isPublishing && !isSavingDraft

    companion object {
        fun create(rating: Rating): BeerRatingViewState {
            return BeerRatingViewState(
                rating = rating,
                isPublishing = false,
                isSavingDraft = false
            )
        }
    }
}
