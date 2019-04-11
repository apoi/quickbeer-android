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

import android.text.format.DateUtils
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import org.threeten.bp.ZonedDateTime
import polanski.option.Option
import quickbeer.android.data.pojos.Beer
import quickbeer.android.data.pojos.Brewer
import quickbeer.android.data.pojos.ItemList
import quickbeer.android.utils.kotlin.within

enum class WithinTime(val time: Long) {
    MONTH(30 * DateUtils.DAY_IN_MILLIS),
    WEEK(7 * DateUtils.DAY_IN_MILLIS),
    SECONDS(10 * DateUtils.SECOND_IN_MILLIS) // For testing
}

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

class Something<T> : Validator<Option<T>> {
    override fun validate(): SingleTransformer<Option<T>, Option<T>> {
        return SingleTransformer { source ->
            source.map {
                if (it.isNone) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
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

class ItemListTimeValidator<T>(private val isWithin: WithinTime) : Validator<Option<ItemList<T>>> {
    override fun validate(): SingleTransformer<Option<ItemList<T>>, Option<ItemList<T>>> {
        return SingleTransformer { source ->
            source.map {
                if (it.match({ it.items.isEmpty() || !it.updateDate.within(isWithin.time)}, { true })) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
    }
}

class DateValidator(private val isWithin: WithinTime) : Validator<ZonedDateTime?> {
    override fun validate(): SingleTransformer<ZonedDateTime?, ZonedDateTime?> {
        return SingleTransformer { source ->
            source.map {
                if (!it.within(isWithin.time)) {
                    throw ValidationFailedException()
                } else {
                    it
                }
            }
        }
    }
}

class HasBeerBasicData : Validator<Option<Beer>> {
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

class HasBeerDetailsData : Validator<Option<Beer>> {
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

class HasBrewerDetailsData : Validator<Option<Brewer>> {
    override fun validate(): SingleTransformer<Option<Brewer>, Option<Brewer>> {
        return SingleTransformer { source ->
            source.map {
                if (it.match({ !it.hasDetails() }, { true })) {
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

fun Completable.onValidationError(block: Completable): Completable {
    return onErrorResumeNext {
        if (it is ValidationFailedException) {
            block
        } else Completable.error(it)
    }
}

class ValidationFailedException : Throwable()
