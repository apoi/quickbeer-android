/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package quickbeer.android.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import polanski.option.Option
import polanski.option.OptionUnsafe
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.network.fetchers.impl.BeerFetcher
import timber.log.Timber

interface Validator<T> {
    fun validate(): SingleTransformer<T, T>
}

class Accept<T> : Validator<T> {
    override fun validate(): SingleTransformer<T, T> {
        return SingleTransformer { source -> source }
    }
}

class Reject<T> : Validator<T> {
    override fun validate(): SingleTransformer<T, T> {
        return SingleTransformer { Single.error(ValidationFailedException()) }
    }
}

class NotEmpty<T> : Validator<Option<ItemList<T>>> {
    override fun validate(): SingleTransformer<Option<ItemList<T>>, Option<ItemList<T>>> {
        return SingleTransformer { source ->
            source.map {
                if (it.match({ it.items.isEmpty() }, { true })) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
    }
}

class HasBasicData : Validator<Option<Beer>> {
    override fun validate(): SingleTransformer<Option<Beer>, Option<Beer>> {
        return SingleTransformer { source ->
            source.map {
                if (it.match({ it.basicDataMissing() }, { true })) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
    }
}

class HasDetailsData : Validator<Option<Beer>> {
    override fun validate(): SingleTransformer<Option<Beer>, Option<Beer>> {
        return SingleTransformer { source ->
            source.map {
                if (it.match({ it.detailedDataMissing() }, { true })) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
    }
}

fun <T> Single<T>.validate(validator: Validator<T>): Completable {
    return compose(validator.validate())
        .ignoreElement()
}

fun Completable.onValidationError(block: () -> Unit): Completable {
    return onErrorComplete {
        if (it is ValidationFailedException) {
            block()
            true
        } else false
    }
}

class ValidationFailedException : Throwable()
