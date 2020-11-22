package quickbeer.android.feature.topbeers

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import org.koin.android.ext.android.inject
import quickbeer.android.R
import quickbeer.android.databinding.BeerListFragmentBinding
import quickbeer.android.feature.shared.adapter.BeerListModel
import quickbeer.android.feature.shared.adapter.BeerListTypeFactory
import quickbeer.android.ui.DividerDecoration
import quickbeer.android.ui.adapter.simple.ListAdapter
import quickbeer.android.ui.search.SearchFragment
import quickbeer.android.ui.search.SearchViewModel
import quickbeer.android.util.ktx.viewBinding

class TopBeersFragment : SearchFragment(R.layout.beer_list_fragment) {

    private val binding by viewBinding(BeerListFragmentBinding::bind)
    private val photoAdapter = ListAdapter<BeerListModel>(BeerListTypeFactory())
    private val viewModel: TopBeersViewModel by inject()

    override val searchHint = R.string.search_hint

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
            adapter = photoAdapter
            layoutManager = LinearLayoutManager(context)

            setHasFixedSize(true)
            addItemDecoration(DividerDecoration(context))
        }
    }

    override fun toolbar(): MaterialToolbar {
        return binding.appbar.toolbar
    }

    override fun searchViewModel(): SearchViewModel {
        return viewModel
    }

    override fun observeViewState() {
        observe(viewModel.viewState, photoAdapter::setItems)
    }
}
