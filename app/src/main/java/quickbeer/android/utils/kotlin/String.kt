/*
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.utils.kotlin

import polanski.option.Option
import java.util.concurrent.Callable

private val ESCAPE_PATTERNS = listOf("(&#[0-9];{2})", "(&#[0-9]{2})")
private val ESCAPE_CHARS = listOf("(&quot;)", "(&quot)", "(&apos;)", "(&apos)")

fun String?.hasValue(): Boolean {
    return this != null && !this.isEmpty()
}

fun String?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

fun String?.orDefault(default: String): String {
    return if (this != null && !this.isEmpty()) this else default
}

fun CharSequence.isNumeric(): Boolean {
    try {
        Integer.valueOf(this.toString())
        return true
    } catch (ignored: Exception) {
        return false
    }
}

fun String?.emptyAsNone(): Option<String> {
    return if (this.hasValue())
        Option.ofObj(this)
    else
        Option.none()
}

fun Callable<String>.emptyAsNone(): Option<String> {
    try {
        return this.call().emptyAsNone()
    } catch (ignored: Exception) {
        return Option.none()
    }
}

fun String.capitalizeWords(delimiter: String): String {
    return toLowerCase()
        .split(delimiter)
        .joinToString(delimiter) { it.capitalize() }
}

fun String.fixEncoding(): String {
    return this
        .replace("%2C", ",")
        .let { fixEncoding(it, "quot", "\"") }
        .let { fixEncoding(it, "apos", "'") }
}

private fun fixEncoding(input: String, code: String, replacement: String): String {
    return input.replace("&$code;", replacement)
        .replace("&$code", replacement)
}
