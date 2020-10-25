package quickbeer.android.ui.adapter.simple

interface ListItem {

    fun id(): Long

    fun type(factory: ListTypeFactory): Int
}
