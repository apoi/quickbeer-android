package quickbeer.android.feature.recentbeers

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import org.koin.android.ext.android.inject
import quickbeer.android.R
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

class RecentBeersFragment : BaseFragment(R.layout.beer_list_fragment) {

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val photoAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    private val viewModel: RecentBeersViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
        }
    }

    override fun observeViewState() {
        observe(viewModel.viewState, photoAdapter::setItems)
    }
}
