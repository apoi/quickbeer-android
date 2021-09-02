package quickbeer.android.util

import android.content.Context
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.io.BufferedReader

class ResourceProvider(private val context: Context) {

    fun getString(@StringRes stringId: Int): String {
        return context.getString(stringId)
    }

    fun getRaw(@RawRes resId: Int): String {
        return context.resources.openRawResource(resId)
            .bufferedReader()
            .use(BufferedReader::readText)
    }
}
