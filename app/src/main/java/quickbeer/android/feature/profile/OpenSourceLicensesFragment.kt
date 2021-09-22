package quickbeer.android.feature.profile

import android.os.Bundle
import android.view.View
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.databinding.LicenseListFragmentBinding
import quickbeer.android.feature.profile.model.LicenseListItemModel
import quickbeer.android.feature.profile.model.LicenseTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class OpenSourceLicensesFragment : BaseFragment(R.layout.license_list_fragment) {

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val binding by viewBinding(LicenseListFragmentBinding::bind)
    private val licenseAdapter = ListAdapter<LicenseListItemModel>(LicenseTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.toolbar.title = getString(R.string.open_source_title)

        binding.licenseList.apply {
            adapter = licenseAdapter
            addItemDecoration(DividerDecoration(context))
        }

        setLicenses()
    }

    private fun setLicenses() {
        val type = Types.newParameterizedType(List::class.java, LicenseListItemModel::class.java)
        val adapter = moshi.adapter<List<LicenseListItemModel>>(type)
        val json = resourceProvider.getRaw(R.raw.licenses_dependencies)
        val licenses = adapter.fromJson(json).orEmpty().sortedBy(LicenseListItemModel::project)

        licenseAdapter.setItems(licenses)
    }
}
