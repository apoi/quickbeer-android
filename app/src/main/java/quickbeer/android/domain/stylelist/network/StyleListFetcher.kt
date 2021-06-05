package quickbeer.android.domain.stylelist.network

import quickbeer.android.data.fetcher.SingleFetcher
import quickbeer.android.domain.style.Style
import quickbeer.android.network.RateBeerApi

class StyleListFetcher(
    api: RateBeerApi
) : SingleFetcher<List<Style>, List<StyleJson>>(StyleListJsonMapper, { api.styles() })
