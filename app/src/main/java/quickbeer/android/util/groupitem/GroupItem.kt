package quickbeer.android.util.groupitem

interface GroupItem<T> {

    val position: Position

    fun setPosition(position: Position): T

    enum class Position {
        ONLY,
        FIRST,
        MIDDLE,
        LAST
    }
}
