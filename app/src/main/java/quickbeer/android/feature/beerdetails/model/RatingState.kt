package quickbeer.android.feature.beerdetails.model

sealed class RatingState<out T> {

    data object Hide : RatingState<Nothing>()

    data object ShowAction : RatingState<Nothing>()

    data object ShowEmptyRating : RatingState<Nothing>()

    data class ShowRating<out T>(val value: T) : RatingState<T>()

    fun forceShow(): RatingState<T> {
        return when (this) {
            is ShowRating -> this
            else -> ShowEmptyRating
        }
    }

    fun valueOrNull(): T? {
        return when (this) {
            is ShowRating -> value
            else -> null
        }
    }
}
