package quickbeer.android.feature.beerdetails.model

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country

data class Address(
    val countryId: Int,
    val country: String?,
    val city: String?,
    val address: String?
) {

    fun cityAndCountry(): String? {
        return when {
            city != null && country != null -> "$city, $country"
            city != null -> city
            country != null -> country
            else -> null
        }
    }

    companion object {
        fun from(country: Country, brewer: Brewer): Address {
            return Address(
                countryId = country.id,
                country = country.name,
                city = brewer.city,
                address = brewer.address
            )
        }
    }
}
