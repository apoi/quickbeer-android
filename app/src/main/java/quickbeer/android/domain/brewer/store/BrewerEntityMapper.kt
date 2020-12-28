package quickbeer.android.domain.brewer.store

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.util.Mapper

object BrewerEntityMapper : Mapper<Brewer, BrewerEntity> {

    override fun mapFrom(source: Brewer): BrewerEntity {
        return BrewerEntity(
            id = source.id,
            name = source.name,
            description = source.description,
            address = source.address,
            city = source.city,
            stateId = source.stateId,
            countryId = source.countryId,
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
            regionId = source.regionId
        )
    }

    override fun mapTo(source: BrewerEntity): Brewer {
        return Brewer(
            id = source.id,
            name = source.name,
            description = source.description,
            address = source.address,
            city = source.city,
            stateId = source.stateId,
            countryId = source.countryId,
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
            regionId = source.regionId
        )
    }
}
