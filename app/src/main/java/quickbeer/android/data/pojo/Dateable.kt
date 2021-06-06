package quickbeer.android.data.pojo

import android.os.Parcelable
import org.threeten.bp.ZonedDateTime

interface Dateable : Parcelable {
    val updated: ZonedDateTime?
    val accessed: ZonedDateTime?
}
