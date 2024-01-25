package quickbeer.android.domain.style

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import quickbeer.android.data.store.Merger

@Parcelize
data class Style(
    val id: Int,
    val name: String,
    val description: String?,
    val parent: Int?,
    val category: Int?
) : Parcelable {

    val code: String
        get() = name.take(2)

    companion object {
        val merger: Merger<Style> = { _, new -> new }
    }
}
