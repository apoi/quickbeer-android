package quickbeer.android.domain.address

import quickbeer.android.domain.brewer.Brewer
import quickbeer.android.domain.country.Country
import quickbeer.android.domain.place.Place
import quickbeer.android.util.ktx.nullIfEmpty

data class Address(
    val countryId: Int,
    val country: String?,
    val city: String?,
    val address: String?,
    val code: String?
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
                address = brewer.address.nullIfEmpty(),
                code = country.code.nullIfEmpty()
            )
        }

        fun from(place: Place, country: Country): Address {
            return Address(
                countryId = country.id,
                country = country.name.nullIfEmpty(),
                city = place.city.nullIfEmpty(),
                address = place.address.nullIfEmpty(),
                code = country.code.nullIfEmpty()
            )
        }
    }
}
