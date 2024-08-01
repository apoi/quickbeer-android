package quickbeer.android.network.adapter

import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import com.squareup.moshi.FromJson

class StripHtmlStringAdapter {

    @FromJson
    fun deserialize(json: String): String {
        return HtmlCompat.fromHtml(json, FROM_HTML_MODE_LEGACY).toString().trim { it <= ' ' }
    }
}
