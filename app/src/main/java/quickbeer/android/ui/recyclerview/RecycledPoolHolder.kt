package quickbeer.android.ui.recyclerview

import androidx.recyclerview.widget.RecyclerView

interface RecycledPoolHolder {

    fun getPool(
        poolType: PoolType,
        creator: () -> RecyclerView.RecycledViewPool
    ): RecyclerView.RecycledViewPool

    enum class PoolType(val id: Int) {
        BEER_LIST(1),
        BREWER_LIST(2)
    }
}
