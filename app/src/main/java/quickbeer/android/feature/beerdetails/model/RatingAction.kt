package quickbeer.android.feature.beerdetails.model

import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import quickbeer.android.R
import quickbeer.android.ui.actionmenu.Action

@Parcelize
sealed class RatingAction(
    open val beerId: Int,
    open val ratingId: Int? = null,
    @StringRes override val text: Int,
    @StringRes val confirmTitle: Int? = null,
    @StringRes val confirmMessage: Int? = null
) : Action(text) {

    data class CreateDraft(override val beerId: Int) : RatingAction(
        beerId = beerId,
        text = R.string.rating_action_rate
    )

    data class CreateTick(override val beerId: Int) : RatingAction(
        beerId = beerId,
        text = R.string.rating_action_tick
    )

    data class EditDraft(override val beerId: Int, override val ratingId: Int) : RatingAction(
        beerId = beerId,
        ratingId = ratingId,
        text = R.string.edit_draft
    )

    data class DeleteDraft(override val beerId: Int, override val ratingId: Int) : RatingAction(
        beerId = beerId,
        ratingId = ratingId,
        text = R.string.delete_draft,
        confirmTitle = R.string.delete_draft,
        confirmMessage = R.string.delete_draft_message
    )

    data class EditRating(override val beerId: Int, override val ratingId: Int) : RatingAction(
        beerId = beerId,
        ratingId = ratingId,
        text = R.string.edit_rating
    )
}
