package quickbeer.android.feature.beerdetails.model

/**
 * State based on current data
 */
sealed class RatingState<out T> {

    data object Hide : RatingState<Nothing>()

    data object ShowAction : RatingState<Nothing>()

    data class ShowRating<out T>(val value: T) : RatingState<T>()

    fun valueOrNull(): T? {
        return when (this) {
            is ShowRating -> value
            else -> null
        }
    }
}
