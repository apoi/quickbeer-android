package quickbeer.android.domain.beer.validator

import quickbeer.android.data.repository.Validator
import quickbeer.android.domain.beer.Beer

open class HasBasicData : Validator<Beer> {
    override fun validate(beer: Beer): Boolean {
        return beer.brewerId != null && !beer.styleName.isNullOrEmpty()
    }
}

class HasDetailedData : HasBasicData() {
    override fun validate(beer: Beer): Boolean {
        return super.validate(beer) && beer.description != null
    }
}
