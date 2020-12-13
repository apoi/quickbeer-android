package quickbeer.android.ui.recyclerview

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView

class DefaultRecycledPoolHolder : RecycledPoolHolder {

    private val pools = SparseArray<RecyclerView.RecycledViewPool>()

    override fun getPool(
        poolType: RecycledPoolHolder.PoolType,
        creator: () -> RecyclerView.RecycledViewPool
    ): RecyclerView.RecycledViewPool {
        return pools[poolType.id] ?: creator.invoke().also {
            pools.put(poolType.id, it)
        }
    }
}
