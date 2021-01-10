package quickbeer.android.ui.listener

import com.google.android.material.tabs.TabLayout

class OnTabSelected(
    private val callback: () -> Unit
) : TabLayout.OnTabSelectedListener {

    override fun onTabSelected(tab: TabLayout.Tab) = callback.invoke()

    override fun onTabUnselected(tab: TabLayout.Tab) = Unit

    override fun onTabReselected(tab: TabLayout.Tab) = callback.invoke()
}
