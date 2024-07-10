package quickbeer.android.domain.rating

import android.content.Context
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import quickbeer.android.R
import quickbeer.android.databinding.RatingListItemBinding
import quickbeer.android.util.ktx.formatDate

/**
 * Binds rating data to the matching view, RatingListItem.
 */
object RatingBinder {

    fun bind(
        context: Context,
        rating: Rating,
        binding: RatingListItemBinding,
        showActions: Boolean = false
    ) {
        binding.description.text = rating.comments
        binding.actions.isVisible = showActions

        setHeader(context, rating, binding, showActions)
        setDetails(rating, binding)

        binding.description.isVisible = !rating.comments.isNullOrBlank()
        binding.detailsRow.isVisible = rating.appearance != null
    }

    private fun setHeader(
        context: Context,
        rating: Rating,
        binding: RatingListItemBinding,
        showActions: Boolean
    ) {
        binding.user.text = formatMetadata(rating)
        binding.score.text = formatTotalScore(rating)

        setHeaderElementsVisibility(context, rating, binding, showActions)

        binding.avatar.load(rating.avatarUri()) {
            crossfade(context.resources.getInteger(android.R.integer.config_shortAnimTime))
            transformations(CircleCropTransformation())
        }
    }

    private fun formatMetadata(rating: Rating): String {
        return rating.userName +
            (rating.country?.let { ", $it" } ?: "") + "\n" +
            (rating.timeUpdated ?: rating.timeEntered)?.formatDate()
    }

    private fun formatTotalScore(rating: Rating): String {
        val totalScore = when {
            rating.isDraft() -> rating.draftTotalScore()
            else -> rating.totalScore
        }

        return totalScore
            ?.let { String.format("%.1f", it) }
            ?: "?"
    }

    private fun setHeaderElementsVisibility(
        context: Context,
        rating: Rating,
        binding: RatingListItemBinding,
        showActions: Boolean
    ) {
        val isPublished = !showActions || (!rating.isDraft() && !rating.isPrivateRating())

        binding.avatar.isVisible = isPublished
        binding.user.isVisible = isPublished
        binding.draftLayout.isVisible = !isPublished

        if (isPublished) {
            binding.ratingItemLayout.setBackgroundResource(0)
        } else if (rating.isDraft()) {
            binding.draftTitle.text = context.getString(R.string.rating_draft)
            binding.draftMessage.text = context.getString(R.string.rating_draft_explanation)
            binding.ratingItemLayout.setBackgroundResource(R.drawable.border_dashed)
        } else if (rating.isPrivateRating()) {
            binding.draftTitle.text = context.getString(R.string.rating_private)
            binding.draftMessage.text = context.getString(R.string.rating_private_explanation)
            binding.ratingItemLayout.setBackgroundResource(R.drawable.border_dashed)
        }
    }

    private fun setDetails(rating: Rating, binding: RatingListItemBinding) {
        binding.appearance.text = java.lang.String.valueOf(rating.appearance)
        binding.aroma.text = java.lang.String.valueOf(rating.aroma)
        binding.flavor.text = java.lang.String.valueOf(rating.flavor)
        binding.mouthfeel.text = java.lang.String.valueOf(rating.mouthfeel)
        binding.overall.text = java.lang.String.valueOf(rating.overall)
    }
}
