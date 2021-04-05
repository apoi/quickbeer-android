package quickbeer.android.ui.listener

import com.google.android.material.tabs.TabLayout

class OnTabSelected(
    private val callback: (Int) -> Unit
) : TabLayout.OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab) = callback.invoke(tab.position)

    override fun onTabUnselected(tab: TabLayout.Tab) = Unit

    override fun onTabReselected(tab: TabLayout.Tab) = callback.invoke(tab.position)
}
