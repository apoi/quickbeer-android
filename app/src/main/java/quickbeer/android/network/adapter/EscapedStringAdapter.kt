package quickbeer.android.network.adapter

import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.squareup.moshi.FromJson
import java.util.regex.Matcher
import java.util.regex.Pattern

class EscapedStringAdapter {

    @FromJson
    fun deserialize(json: String): String {
        val value = PATTERN.matcher(json).replaceAll(Matcher.quoteReplacement("<p>"))
        return HtmlCompat.fromHtml(value, FROM_HTML_MODE_LEGACY).toString().trim { it <= ' ' }
    }

    companion object {
        private val PATTERN = Pattern.compile("<br ?/?>", Pattern.CASE_INSENSITIVE)
    }
}
