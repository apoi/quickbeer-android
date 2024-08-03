package quickbeer.android.util.ktx

import quickbeer.android.util.groupitem.GroupItem
import quickbeer.android.util.groupitem.GroupItem.Position

fun <T : GroupItem<T>> List<T>.groupItems(): List<T> {
    return when {
        isEmpty() -> this
        size == 1 -> listOf(first().setPosition(Position.ONLY))
        else -> {
            val first = first().setPosition(Position.FIRST)
            val last = last().setPosition(Position.LAST)
            val middle = subList(1, size - 1).map { it.setPosition(Position.MIDDLE) }
            listOf(first) + middle + last
        }
    }
}
