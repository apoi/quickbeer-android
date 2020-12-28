package quickbeer.android.feature.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SearchPagerAdapter(fm: FragmentManager, private val query: String?) :
    FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchBeersFragment.create(query)
            1 -> SearchBeersFragment.create(query)
            else -> SearchBeersFragment.create(query)
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Beers"
            1 -> "Brewers"
            else -> "Styles"
        }
    }
}
