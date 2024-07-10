package quickbeer.android.feature.beerdetails.model

/**
 * State of user's ticking actions
 */
sealed class TickActionState {

    data object Default : TickActionState()

    data object ActionClicked : TickActionState()

    data object LoadingInProgress : TickActionState()

    fun showCard(): Boolean {
        return this is ActionClicked || this is LoadingInProgress
    }
}
