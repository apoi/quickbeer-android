package quickbeer.android.domain.beer

import android.os.Parcelable
import kotlin.math.roundToInt
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.repository.Validator
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
        return Constants.BEER_IMAGE_PATH.format(id)
    }

    fun isTicked(): Boolean {
        return tickValue != null
    }

    companion object {
        val merger: Merger<Beer> = { old, new ->
            Beer(
                id = new.id,
                name = new.name ?: old.name,
                brewerId = new.brewerId ?: old.brewerId,
                brewerName = new.brewerName ?: old.brewerName,
                contractBrewerId = new.contractBrewerId ?: old.contractBrewerId,
                contractBrewerName = new.contractBrewerName ?: old.contractBrewerName,
                averageRating = new.averageRating ?: old.averageRating,
                overallRating = new.overallRating ?: old.overallRating,
                styleRating = new.styleRating ?: old.styleRating,
                rateCount = new.rateCount ?: old.rateCount,
                countryId = new.countryId ?: old.countryId,
                styleId = new.styleId ?: old.styleId,
                styleName = new.styleName ?: old.styleName,
                alcohol = new.alcohol ?: old.alcohol,
                ibu = new.ibu ?: old.ibu,
                description = new.description ?: old.description,
                isAlias = new.isAlias ?: old.isAlias,
                isRetired = new.isRetired ?: old.isRetired,
                isVerified = new.isVerified ?: old.isVerified,
                unrateable = new.unrateable ?: old.unrateable,
                tickValue = new.tickValue ?: old.tickValue,
                tickDate = new.tickDate ?: old.tickDate
            )
        }
    }

    open class BasicDataValidator : Validator<Beer> {
        override fun validate(beer: Beer?): Boolean {
            return beer?.brewerId != null && beer.styleName != null
        }
    }

    class DetailsDataValidator : BasicDataValidator() {
        override fun validate(beer: Beer?): Boolean {
            return super.validate(beer) && beer?.description != null
        }
    }
}
