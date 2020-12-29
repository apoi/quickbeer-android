package quickbeer.android.domain.countrylist.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import quickbeer.android.R
import quickbeer.android.data.fetcher.SingleFetcher
import quickbeer.android.domain.country.Country
import quickbeer.android.network.result.ApiResult
import quickbeer.android.util.ResourceProvider

class CountryListFetcher(
    resourceProvider: ResourceProvider,
    moshi: Moshi
) : SingleFetcher<List<Country>, List<CountryJson>>(
    CountryListJsonMapper,
    {
        val listType = Types.newParameterizedType(List::class.java, CountryJson::class.java)
        val adapter = moshi.adapter<List<CountryJson>>(listType)
        val json = resourceProvider.getRaw(R.raw.countries)
        val countries = adapter.fromJson(json)
        ApiResult.Success(countries)
    }
)
