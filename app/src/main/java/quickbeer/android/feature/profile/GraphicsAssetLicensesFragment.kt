package quickbeer.android.feature.profile

import android.os.Bundle
import android.view.View
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.databinding.LicenseListFragmentBinding
import quickbeer.android.feature.profile.model.LicenseDataModel
import quickbeer.android.feature.profile.model.LicenseTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class GraphicsAssetLicensesFragment : BaseFragment(R.layout.license_list_fragment) {

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val binding by viewBinding(LicenseListFragmentBinding::bind)
    private val licenseAdapter = ListAdapter<LicenseDataModel>(LicenseTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.toolbar.title = getString(R.string.graphics_assets_title)

        binding.licenseList.apply {
            adapter = licenseAdapter
            addItemDecoration(DividerDecoration(context))
        }

        setLicenses()
    }

    private fun setLicenses() {
        val type = Types.newParameterizedType(List::class.java, LicenseDataModel::class.java)
        val adapter = moshi.adapter<List<LicenseDataModel>>(type)
        val json = resourceProvider.getRaw(R.raw.licenses_graphics)
        val licenses = adapter.fromJson(json).orEmpty().sortedBy(LicenseDataModel::project)

        licenseAdapter.setItems(licenses)
    }
}
