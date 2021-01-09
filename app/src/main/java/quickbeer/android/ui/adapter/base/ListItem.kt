package quickbeer.android.ui.adapter.base

interface ListItem {

    fun id(): Long

    fun type(factory: ListTypeFactory): Int
}
