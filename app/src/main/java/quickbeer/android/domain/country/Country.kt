package quickbeer.android.domain.country

import quickbeer.android.data.store.Merger

data class Country(
    val id: Int,
    val name: String,
    val code: String,
    val official: String,
    val refer: String,
    val capital: String,
    val region: String,
    val subregion: String,
    val wikipedia: String,
    val wikipediaBeer: String?,
    val description: String?
) {

    companion object {
        val merger: Merger<Country> = { _, new -> new }
    }
}
