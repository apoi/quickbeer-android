package quickbeer.android.network.adapter

import com.squareup.moshi.FromJson
import org.apache.commons.text.StringEscapeUtils

class UnescapeHtmlEntitiesStringAdapter {

    @FromJson
    fun deserialize(json: String): String {
        return StringEscapeUtils.unescapeHtml4(json)
    }
}
