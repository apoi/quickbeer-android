package quickbeer.android.domain.user.store

import javax.inject.Inject
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.user.User

class UserStore @Inject constructor(
    userStoreCore: StoreCore<Int, User>,
) : DefaultStore<Int, User>(userStoreCore)
