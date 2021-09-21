package quickbeer.android.feature.profile.model

import android.view.ViewGroup
import quickbeer.android.R
import quickbeer.android.databinding.LicenseListItemBinding
import quickbeer.android.ui.adapter.base.ListItem
import quickbeer.android.ui.adapter.base.ListTypeFactory
import quickbeer.android.ui.adapter.base.ListViewHolder

class LicenseTypeFactory : ListTypeFactory() {

    override fun type(item: ListItem): Int {
        return R.layout.license_list_item
    }

    override fun createViewHolder(type: Int, parent: ViewGroup): ListViewHolder<*> {
        return LicenseListItemViewHolder(
            createBinding(LicenseListItemBinding::inflate, parent)
        )
    }
}
