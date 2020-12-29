package quickbeer.android.util

import android.content.Context
import androidx.annotation.RawRes
import java.io.BufferedReader

class ResourceProvider(private val context: Context) {

    fun getRaw(@RawRes resId: Int): String {
        return context.resources.openRawResource(resId)
            .bufferedReader()
            .use(BufferedReader::readText)
    }
}
