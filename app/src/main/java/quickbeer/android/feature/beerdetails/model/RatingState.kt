package quickbeer.android.feature.beerdetails.model

sealed class RatingState<out T> {

    data object Hide : RatingState<Nothing>()

    data object ShowAction : RatingState<Nothing>()

    data class ShowRating<out T>(val value: T) : RatingState<T>()
}