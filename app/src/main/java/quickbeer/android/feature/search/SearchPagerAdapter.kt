package quickbeer.android.feature.search

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SearchPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount(): Int {
        return 4
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchBeersFragment()
            1 -> SearchBrewersFragment()
            2 -> SearchStylesFragment()
            else -> SearchCountriesFragment()
        }
    }
}
