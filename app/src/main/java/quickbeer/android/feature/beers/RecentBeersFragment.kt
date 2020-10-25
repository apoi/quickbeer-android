package quickbeer.android.feature.beers

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import quickbeer.android.R
import quickbeer.android.databinding.RecentBeersFragmentBinding
import quickbeer.android.feature.beers.adapter.BeerListModel
import quickbeer.android.feature.beers.adapter.BeerListTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.base.BaseFragment
import quickbeer.android.util.ktx.viewBinding

class RecentBeersFragment : BaseFragment(R.layout.recent_beers_fragment) {

    private val binding by viewBinding(RecentBeersFragmentBinding::bind)
    private val photoAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())

    private val viewModel: RecentBeersViewModel by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.aboutRecyclerView.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
        }

        binding.listFab.setOnClickListener {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }
    }

    override fun observeViewState() {
        observe(viewModel.viewState, photoAdapter::setItems)
    }
}
