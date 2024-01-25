package quickbeer.android.domain.brewer

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater

@Parcelize
data class Brewer(
    val id: Int,
    val name: String?,
    val description: String?,
    val address: String?,
    val city: String?,
    val stateId: Int?,
    val countryId: Int?,
    val zipCode: String?,
    val typeId: Int?,
    val type: String?,
    val website: String?,
    val facebook: String?,
    val twitter: String?,
    val email: String?,
    val phone: String?,
    val barrels: Int?,
    val founded: ZonedDateTime?,
    val enteredOn: ZonedDateTime?,
    val enteredBy: Int?,
    val logo: String?,
    val viewCount: String?,
    val score: Int?,
    val outOfBusiness: Boolean?,
    val retired: Boolean?,
    val areaCode: String?,
    val hours: String?,
    val headBrewer: String?,
    val metroId: String?,
    val msa: String?,
    val regionId: String?,
    // Application fields
    val normalizedName: String?,
    val updated: ZonedDateTime?,
    val accessed: ZonedDateTime?
) : Parcelable {

    fun imageUri(): String {
        return Constants.BREWER_IMAGE_PATH.format(id)
    }

    companion object {
        val merger: Merger<Brewer> = { old, new ->
            Brewer(
                id = new.id,
                name = new.name ?: old.name,
                description = new.description ?: old.description,
                address = new.address ?: old.address,
                city = new.city ?: old.city,
                stateId = new.stateId ?: old.stateId,
                countryId = new.countryId ?: old.countryId,
                zipCode = new.zipCode ?: old.zipCode,
                typeId = new.typeId ?: old.typeId,
                type = new.type ?: old.type,
                website = new.website ?: old.website,
                facebook = new.facebook ?: old.facebook,
                twitter = new.twitter ?: old.twitter,
                email = new.email ?: old.email,
                phone = new.phone ?: old.phone,
                barrels = new.barrels ?: old.barrels,
                founded = new.founded ?: old.founded,
                enteredOn = new.enteredOn ?: old.enteredOn,
                enteredBy = new.enteredBy ?: old.enteredBy,
                logo = new.logo ?: old.logo,
                viewCount = new.viewCount ?: old.viewCount,
                score = new.score ?: old.score,
                outOfBusiness = new.outOfBusiness ?: old.outOfBusiness,
                retired = new.retired ?: old.retired,
                areaCode = new.areaCode ?: old.areaCode,
                hours = new.hours ?: old.hours,
                headBrewer = new.headBrewer ?: old.headBrewer,
                metroId = new.metroId ?: old.metroId,
                msa = new.msa ?: old.msa,
                regionId = new.regionId ?: old.regionId,
                normalizedName = new.normalizedName ?: old.normalizedName,
                updated = new.updated.orLater(old.updated),
                accessed = new.accessed.orLater(old.accessed)
            )
        }
    }

    open class BasicDataValidator : Validator<Brewer> {
        override suspend fun validate(brewer: Brewer?): Boolean {
            return brewer?.name != null && brewer.countryId != null
        }
    }

    open class DetailsDataValidator : Validator<Brewer> {
        override suspend fun validate(brewer: Brewer?): Boolean {
            return brewer?.enteredOn != null
        }
    }
}
