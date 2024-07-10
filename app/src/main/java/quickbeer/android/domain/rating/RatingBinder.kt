package quickbeer.android.domain.rating

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
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
        val metadata = rating.userName +
            (rating.country?.let { ", $it" } ?: "") + "\n" +
            (rating.timeUpdated ?: rating.timeEntered)?.formatDate()

        binding.description.text = rating.comments
        binding.actions.isVisible = showActions

        if (!rating.isDraft()) {
            binding.draftTitle.isVisible = false
            binding.user.text = metadata
            binding.score.text = String.format("%.1f", rating.totalScore)

            binding.avatar.load(rating.avatarUri()) {
                crossfade(context.resources.getInteger(android.R.integer.config_shortAnimTime))
                transformations(CircleCropTransformation())
            }
        } else {
            binding.score.isVisible = false
            binding.summaryDivider.divider.isVisible = false
            binding.avatar.isVisible = false
            binding.user.isVisible = false
            binding.draftTitle.isVisible = true
            binding.ratingItemLayout.setBackgroundResource(R.drawable.border_dashed)
        }

        if (rating.appearance != null) {
            setDetails(rating, binding)
            binding.detailsRow.visibility = View.VISIBLE
        } else {
            binding.detailsRow.visibility = View.GONE
        }

        // TODO setColors(context, binding, isOwnReview)
    }

    private fun setDetails(rating: Rating, binding: RatingListItemBinding) {
        binding.appearance.text = java.lang.String.valueOf(rating.appearance)
        binding.aroma.text = java.lang.String.valueOf(rating.aroma)
        binding.flavor.text = java.lang.String.valueOf(rating.flavor)
        binding.mouthfeel.text = java.lang.String.valueOf(rating.mouthfeel)
        binding.overall.text = java.lang.String.valueOf(rating.overall)

        binding.appearanceColumn.setOnClickListener { showToast(R.string.rating_appearance_hint) }
        binding.aromaColumn.setOnClickListener { showToast(R.string.rating_aroma_hint) }
        binding.flavorColumn.setOnClickListener { showToast(R.string.rating_flavor_hint) }
        binding.mouthfeelColumn.setOnClickListener { showToast(R.string.rating_mouthfeel_hint) }
        binding.overallColumn.setOnClickListener { showToast(R.string.rating_overall_hint) }
    }

    private fun setColors(context: Context, binding: RatingListItemBinding, isOwnReview: Boolean) {
        val color = if (isOwnReview) {
            context.getColor(R.color.orange_dark)
        } else {
            context.getColor(R.color.gray_30)
        }

        binding.score.setTextColor(color)
        binding.appearance.setTextColor(color)
        binding.aroma.setTextColor(color)
        binding.flavor.setTextColor(color)
        binding.flavor.setTextColor(color)
        binding.mouthfeel.setTextColor(color)
        binding.overall.setTextColor(color)
    }

    private fun showToast(@StringRes resource: Int) {
        // toastProvider.showCancelableToast(resource, Toast.LENGTH_LONG)
    }
}
