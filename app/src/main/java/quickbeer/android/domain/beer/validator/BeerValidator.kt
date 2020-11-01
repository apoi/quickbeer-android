package quickbeer.android.domain.beer.validator

import quickbeer.android.data.repository.Validator
import quickbeer.android.domain.beer.Beer

class HasBasicData : Validator<Beer> {
    override fun validate(value: Beer): Boolean {
        return !value.basicDataMissing()
    }
}
