package quickbeer.android.domain.place

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.threeten.bp.ZonedDateTime
import quickbeer.android.data.store.Merger
import quickbeer.android.util.ktx.orLater

@Parcelize
data class Place(
    val id: Int,
    val name: String?,
    val normalizedName: String?,
    val updated: ZonedDateTime?,
    val accessed: ZonedDateTime?
) : Parcelable {

    companion object {
        val merger: Merger<Place> = { old, new ->
            Place(
                id = new.id,
                name = new.name ?: old.name,
                normalizedName = new.normalizedName ?: old.normalizedName,,
                updated = new.updated.orLater(old.updated),
                accessed = new.accessed.orLater(old.accessed)
            )
        }
    }
}
