package quickbeer.android.feature.profile.model

import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory

class OpenSourceLicenseModel(
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
