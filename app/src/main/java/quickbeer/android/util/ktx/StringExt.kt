package quickbeer.android.util.ktx

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

private val SINGLE_LINEBREAK_PATTERN = "(?<!\\n)\\n(?!\\n)".toRegex()
private val NORMALIZER_PATTERN = Pattern.compile("\\p{M}")
private val SPECIAL_CHARS = """["“”„…'‘’«»()-.,:;/\\|¨°%#&?!=]""".toRegex()

fun String?.nullIfEmpty(): String? {
    return if (this.isNullOrEmpty()) null else this
}

fun String.removeSingleLineBreaks(): String {
    return replace(SINGLE_LINEBREAK_PATTERN, " ")
}

/**
 * Normalize and simplify for search purposes.
 */
fun String?.normalize(): String? {
    if (this == null) return null
    return NORMALIZER_PATTERN
        .matcher(Normalizer.normalize(this, Normalizer.Form.NFD))
        .replaceAll("")
        .toLowerCase(Locale.ROOT)
        .replace("ø", "o")
        .replace("ł", "l")
        .replace("æ", "ae")
        .replace("ß", "ss")
        .replace(SPECIAL_CHARS, "")
        .trim()
}
