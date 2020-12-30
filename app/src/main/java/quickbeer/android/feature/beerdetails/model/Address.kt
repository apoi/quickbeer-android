package quickbeer.android.feature.beerdetails.model

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country
import quickbeer.android.util.ktx.nullIfEmpty

data class Address(
    val countryId: Int,
    val country: String?,
    val city: String?,
    val address: String?
) {

    fun cityAndCountry(): String? {
        return when {
            country != null && city != null -> "$city, $country"
            country != null -> country
            city != null -> city
            else -> null
        }
    }

    companion object {
        fun from(brewer: Brewer, country: Country): Address {
            return Address(
                countryId = country.id,
                country = country.name.nullIfEmpty(),
                city = brewer.city.nullIfEmpty(),
                address = brewer.address.nullIfEmpty()
            )
        }
    }
}
