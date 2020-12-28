package quickbeer.android.domain.brewer

import android.os.Parcelable
import java.util.Locale
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.Constants
import quickbeer.android.data.repository.Validator
import quickbeer.android.data.store.Merger

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
    val regionId: String?
) : Parcelable {

    fun getImageUri(): String {
        return String.format(Locale.ROOT, Constants.BREWER_IMAGE_PATH, id)
    }

    companion object {
        val merger: Merger<Brewer> = { old, new ->
            Brewer(
                id = new.id ?: old.id,
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
                regionId = new.regionId ?: old.regionId
            )
        }
    }

    open class BasicDataValidator : Validator<Brewer> {
        override fun validate(brewer: Brewer?): Boolean {
            return brewer?.name != null
        }
    }
}
