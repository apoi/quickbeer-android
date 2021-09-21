package quickbeer.android.feature.profile

import android.os.Bundle
import android.view.View
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import quickbeer.android.R
import quickbeer.android.databinding.OpenSourceLicenseFragmentBinding
import quickbeer.android.feature.profile.model.OpenSourceLicenseModel
import quickbeer.android.feature.profile.model.OpenSourceLicenseTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.base.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ResourceProvider
import quickbeer.android.util.ktx.viewBinding

@AndroidEntryPoint
class OpenSourceLicensesFragment : BaseFragment(R.layout.open_source_license_fragment) {

    @Inject
    lateinit var moshi: Moshi

    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val binding by viewBinding(OpenSourceLicenseFragmentBinding::bind)
    private val licenseAdapter = ListAdapter<OpenSourceLicenseModel>(OpenSourceLicenseTypeFactory())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.licenseList.apply {
            adapter = licenseAdapter
            addItemDecoration(DividerDecoration(context))
        }

        setLicenses()
    }

    private fun setLicenses() {
        val type = Types.newParameterizedType(List::class.java, OpenSourceLicenseModel::class.java)
        val adapter = moshi.adapter<List<OpenSourceLicenseModel>>(type)
        val json = resourceProvider.getRaw(R.raw.open_source_licenses)
        val licenses = adapter.fromJson(json).orEmpty().sortedBy(OpenSourceLicenseModel::project)

        licenseAdapter.setItems(licenses)
    }
}
