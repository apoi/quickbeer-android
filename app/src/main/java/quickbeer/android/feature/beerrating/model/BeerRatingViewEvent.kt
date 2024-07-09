package quickbeer.android.feature.beerrating.model

import androidx.annotation.StringRes

sealed class BeerRatingViewEvent {

    data class ShowMessageAndClose(@StringRes val message: Int) : BeerRatingViewEvent()

    data class ShowError(val cause: Throwable) : BeerRatingViewEvent()
}
