package quickbeer.android.domain.user.repository

import javax.inject.Inject
import quickbeer.android.data.repository.repository.DefaultRepository
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.network.RateCountFetcher
import quickbeer.android.domain.user.store.UserStore

class UserRepository @Inject constructor(
    store: UserStore,
    fetcher: RateCountFetcher,
) : DefaultRepository<Int, User>(store, fetcher::fetch)
