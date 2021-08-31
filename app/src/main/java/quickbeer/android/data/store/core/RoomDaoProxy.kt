package quickbeer.android.data.store.core

import quickbeer.android.data.store.StoreCore

/**
 * Interface for accessing a DAO that implements methods required for a StoreCore. DAO proxies are
 * needed as Room DAOs can't have abstract parent classes that would define the core interface.
 *
 * Put and delete streams can't be implemented by DAO, so the proxy will not provide these. The
 * streams must be implemented by the StoreCore that uses the proxy.
 */
abstract class RoomDaoProxy<K : Any, V : Any> : StoreCore<K, V> {

    final override fun getPutStream() = error("Not implemented")

    final override fun getDeleteStream() = error("Not implemented")
}
