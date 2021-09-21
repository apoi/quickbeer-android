package quickbeer.android.feature.profile.model

import android.text.util.Linkify
import android.widget.TextView
import java.util.regex.Pattern
import quickbeer.android.databinding.LicenseListItemBinding
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.util.ktx.removeSingleLineBreaks

class LicenseListItemViewHolder(
    private val binding: LicenseListItemBinding
) : ListViewHolder<LicenseDataModel>(binding.root) {

    override fun bind(item: LicenseDataModel) {
        binding.project.text = item.project
        addLink(binding.project, item.project, item.link)

        binding.license.text = item.license.removeSingleLineBreaks()
    }

    private fun addLink(view: TextView, pattern: String, link: String) {
        val filter = Linkify.TransformFilter { _, _ -> link }
        Linkify.addLinks(view, Pattern.compile(pattern), null, null, filter)
    }
}
