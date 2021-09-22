package quickbeer.android.feature.profile.model

import quickbeer.android.R
import quickbeer.android.databinding.LicenseListItemBinding
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.util.ktx.removeSingleLineBreaks

class LicenseListItemViewHolder(
    private val binding: LicenseListItemBinding
) : ListViewHolder<LicenseListItemModel>(binding.root) {

    override fun bind(item: LicenseListItemModel) {
        binding.project.text = item.project
        binding.link.text = itemView.context.getString(R.string.license_link).format(item.link)
        binding.license.text = item.license.removeSingleLineBreaks()
    }
}
