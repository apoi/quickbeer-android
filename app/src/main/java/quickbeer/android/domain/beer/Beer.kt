package quickbeer.android.domain.beer

import android.os.Parcelable
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.store.Merger

@Parcelize
data class Beer(
    val id: Int,
    val name: String?,
    val brewerId: Int?,
    val brewerName: String?,
    val contractBrewerId: Int?,
    val contractBrewerName: String?,
    val averageRating: Float?,
    val overallRating: Float?,
    val styleRating: Float?,
    val rateCount: Int?,
    val countryId: Int?,
    val styleId: Int?,
    val styleName: String?,
    val alcohol: Float?,
    val ibu: Float?,
    val description: String?,
    val isAlias: Boolean?,
    val isRetired: Boolean?,
    val isVerified: Boolean?,
    val unrateable: Boolean?,
    val tickValue: Int?,
    val tickDate: ZonedDateTime?
) : Parcelable {

    fun rating(): Int {
        return overallRating
            ?.takeIf { it > 0.0f }
            ?.roundToInt()
            ?: -1
    }

    fun imageUri(): String {
        return String.format(Locale.ROOT, Constants.BEER_IMAGE_PATH, id)
    }

    fun isTicked(): Boolean {
        return tickValue != null
    }

    companion object {
        val merger: Merger<Beer> = { _, new -> new }
    }
}
