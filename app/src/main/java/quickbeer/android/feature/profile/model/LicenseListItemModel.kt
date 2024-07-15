package quickbeer.android.feature.profile.model

import com.squareup.moshi.JsonClass
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

@JsonClass(generateAdapter = true)
data class LicenseListItemModel(
    val project: String,
    val link: String,
    val license: String
) : ListItem {

    override fun id(): Long {
        return project.hashCode().toLong()
    }

    override fun type(factory: ListTypeFactory): Int {
        return factory.type(this)
    }
}
