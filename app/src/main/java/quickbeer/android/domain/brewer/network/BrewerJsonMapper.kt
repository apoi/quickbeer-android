package quickbeer.android.domain.brewer.network

import org.threeten.bp.ZonedDateTime
import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.util.JsonMapper
import quickbeer.android.util.ktx.normalize

object BrewerJsonMapper : JsonMapper<Int, Brewer, BrewerJson> {

    override fun map(key: Int, source: BrewerJson): Brewer {
        return Brewer(
            id = source.id,
            name = source.name,
            description = source.description,
            address = source.address,
            city = source.city,
            stateId = source.stateId ?: source.brewerStateId,
            countryId = source.countryId ?: source.brewerCountryId,
            zipCode = source.zipCode,
            typeId = source.typeId,
            type = source.type,
            website = source.website,
            facebook = source.facebook,
            twitter = source.twitter,
            email = source.email,
            phone = source.phone,
            barrels = source.barrels,
            founded = source.founded,
            enteredOn = source.enteredOn,
            enteredBy = source.enteredBy,
            logo = source.logo,
            viewCount = source.viewCount,
            score = source.score,
            outOfBusiness = source.outOfBusiness,
            retired = source.retired,
            areaCode = source.areaCode,
            hours = source.hours,
            headBrewer = source.headBrewer,
            metroId = source.metroId,
            msa = source.msa,
            regionId = source.regionId,
            normalizedName = source.name.normalize(),
            updated = ZonedDateTime.now(),
            accessed = null
        )
    }
}
