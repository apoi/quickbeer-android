package quickbeer.android.util

/**
 * Interface for mapping between two types.
 */
interface Mapper<T, U> {

    fun mapFrom(source: T): U

    fun mapTo(source: U): T
}
