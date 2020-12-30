package quickbeer.android.util.ktx

import java.text.Normalizer
import java.util.regex.Pattern

fun String?.nullIfEmpty(): String? {
    return if (this.isNullOrEmpty()) null else this
}

fun String?.normalize(): String {
    if (this == null) return ""

    return Pattern.compile("\\p{M}")
        .matcher(Normalizer.normalize(this, Normalizer.Form.NFD))
        .replaceAll("")
        .replace("æ".toRegex(), "ae")
        .replace("Æ".toRegex(), "AE")
        .replace("ß".toRegex(), "ss")
        .replace("ø".toRegex(), "o")
        .replace("Ø".toRegex(), "O")
}
