package quickbeer.android.domain.countrylist.network

import quickbeer.android.domain.country.Country
import quickbeer.android.util.Mapper

object CountryListJsonMapper : Mapper<List<Country>, List<CountryJson>> {

    override fun mapFrom(source: List<Country>): List<CountryJson> {
        return source.map {
            CountryJson(
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

    override fun mapTo(source: List<CountryJson>): List<Country> {
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
