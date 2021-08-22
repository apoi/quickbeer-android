package quickbeer.android.domain.countrylist.network

import quickbeer.android.domain.country.Country
import quickbeer.android.util.JsonMapper

object CountryListJsonMapper : JsonMapper<Unit, List<Country>, List<CountryJson>> {

    override fun map(key: Unit, source: List<CountryJson>): List<Country> {
        return source.map {
            Country(
                id = it.id,
                name = it.name,
                code = it.code,
                official = it.official,
                refer = it.refer,
                capital = it.capital,
                region = it.region,
                subregion = it.subregion,
                wikipedia = it.wikipedia,
                wikipediaBeer = it.wikipediaBeer,
                description = it.description
            )
        }
    }
}
