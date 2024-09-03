package quickbeer.android.ui.binder

import android.content.Context
import quickbeer.android.Constants
import quickbeer.android.R
import quickbeer.android.databinding.DetailsInfoColumnsBinding
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.place.Place
import quickbeer.android.util.LinkUtils
import quickbeer.android.util.ktx.ifNull
import quickbeer.android.util.ktx.openUri

/**
 * Binds data to the matching view, DetailsInfoColumn.
 */
object InfoColumnsBinder {

    private const val VISIBLE = 1.0f
    private const val OPAQUE = 0.4f

    fun bind(
        context: Context,
        place: Place,
        binding: DetailsInfoColumnsBinding,
        onInfoClicked: (() -> Unit)?
    ) {
        bind(
            context = context,
            infoTitle = R.string.percentile_rating,
            infoValue = place.percentile?.toString(),
            website = place.website,
            facebook = place.facebook,
            twitter = place.twitter,
            binding = binding,
            onInfoClicked = onInfoClicked
        )
    }

    fun bind(context: Context, brewer: Brewer, binding: DetailsInfoColumnsBinding) {
        bind(
            context = context,
            infoTitle = R.string.founded,
            infoValue = brewer.founded?.year?.toString(),
            website = brewer.website,
            facebook = brewer.facebook,
            twitter = brewer.twitter,
            binding = binding,
            onInfoClicked = null
        )
    }

    private fun bind(
        context: Context,
        infoTitle: Int,
        infoValue: String?,
        website: String?,
        facebook: String?,
        twitter: String?,
        binding: DetailsInfoColumnsBinding,
        onInfoClicked: (() -> Unit)?
    ) {
        val notAvailable = context.getString(R.string.not_available)

        binding.info.title = context.getString(infoTitle)
        binding.info.value = infoValue ?: notAvailable
        binding.info.setOnClickListener { onInfoClicked?.invoke() }

        website
            ?.takeIf(String::isNotEmpty)
            ?.let(LinkUtils::fixUrl)
            ?.let { url ->
                binding.webContainer.alpha = VISIBLE
                binding.webContainer.setOnClickListener { context.openUri(url) }
            }.ifNull {
                binding.web.alpha = OPAQUE
            }

        facebook
            ?.takeIf(String::isNotEmpty)
            ?.let { Constants.FACEBOOK_PATH.format(it) }
            ?.let { url ->
                binding.facebookContainer.alpha = VISIBLE
                binding.facebookContainer.setOnClickListener { context.openUri(url) }
            }.ifNull {
                binding.facebook.alpha = OPAQUE
            }

        twitter
            ?.takeIf(String::isNotEmpty)
            ?.let { Constants.TWITTER_PATH.format(it) }
            ?.let { url ->
                binding.twitterContainer.alpha = VISIBLE
                binding.twitterContainer.setOnClickListener { context.openUri(url) }
            }.ifNull {
                binding.twitter.alpha = OPAQUE
            }
    }
}
