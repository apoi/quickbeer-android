package quickbeer.android.util.migration.v41

import javax.inject.Inject
import quickbeer.android.domain.preferences.store.IntPreferenceStore
import quickbeer.android.domain.preferences.store.StringPreferenceStore
import quickbeer.android.domain.user.User
import quickbeer.android.domain.user.store.UserStore
import quickbeer.android.util.migration.ApplicationMigration

class MigrationV41 @Inject constructor(
    private val userStore: UserStore,
    private val intPreferenceStore: IntPreferenceStore,
    private val stringPreferenceStore: StringPreferenceStore
) : ApplicationMigration.Migration {

    override suspend fun migrate() {
        // Migration is run only once
        if (intPreferenceStore.get(MIGRATION_V41) != null) return

        intPreferenceStore.put(MIGRATION_V41, 1)

        migrateUser()
    }

    private suspend fun migrateUser() {
        val id = intPreferenceStore.get(USERID)
        val username = stringPreferenceStore.get(USERNAME)
        val password = stringPreferenceStore.get(PASSWORD)

        if (id != null && username != null && password != null) {
            val user = User.createCurrentUser(id, username, password)
            userStore.put(id, user)
        }

        intPreferenceStore.delete(USERID)
        stringPreferenceStore.delete(USERNAME)
        stringPreferenceStore.delete(PASSWORD)
    }

    companion object {
        private const val MIGRATION_V41 = "MIGRATION_V41"

        // Legacy preferences keys
        private const val USERID = "PREF_USERID"
        private const val USERNAME = "PREF_USERNAME"
        private const val PASSWORD = "PREF_PASSWORD"
    }
}
