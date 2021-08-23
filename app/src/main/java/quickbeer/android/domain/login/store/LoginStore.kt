package quickbeer.android.domain.login.store

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import quickbeer.android.data.store.StoreCore
import quickbeer.android.data.store.store.DefaultStore
import quickbeer.android.domain.user.User
import quickbeer.android.util.LegacyPreferences

class LoginStore @Inject constructor(
    userStoreCore: StoreCore<Int, User>,
    preferences: LegacyPreferences
) : DefaultStore<Int, User>(userStoreCore) {

    private var job: Job? = null

    init {
        // Prefill store with current user
        val userId = preferences.userId
        val username = preferences.username

        if (userId != null) {
            job = CoroutineScope(Dispatchers.IO).launch {
                put(userId, User(id = userId, username = username))
            }
        }
    }
}
