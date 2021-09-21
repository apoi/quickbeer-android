package quickbeer.android.feature.profile.model

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.OpenSourceLicenseItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class OpenSourceLicenseTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.open_source_license_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return OpenSourceListViewHolder(
            createBinding(OpenSourceLicenseItemBinding::inflate, parent)
        )
    }
}
