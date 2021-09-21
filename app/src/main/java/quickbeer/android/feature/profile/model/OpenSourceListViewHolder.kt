package quickbeer.android.feature.profile.model

import android.content.Intent
import android.net.Uri
import android.text.util.Linkify
import quickbeer.android.databinding.OpenSourceLicenseItemBinding
import quickbeer.android.ui.adapter.base.ListViewHolder
import quickbeer.android.util.ktx.removeSingleLineBreaks

class OpenSourceListViewHolder(
    private val binding: OpenSourceLicenseItemBinding
) : ListViewHolder<OpenSourceLicenseModel>(binding.root) {

    override fun bind(item: OpenSourceLicenseModel) {
        binding.project.text = item.project
        binding.project.setOnClickListener { openLink(item.link) }

        binding.link.text = item.link
        Linkify.addLinks(binding.link, Linkify.WEB_URLS)

        binding.license.text = item.license.removeSingleLineBreaks()
        Linkify.addLinks(binding.license, Linkify.WEB_URLS)
    }

    private fun openLink(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        itemView.context.startActivity(intent)
    }
}
