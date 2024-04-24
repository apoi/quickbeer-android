package quickbeer.android.util.migration

import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import quickbeer.android.util.migration.v40.MigrationV40
import quickbeer.android.util.migration.v41.MigrationV41

class ApplicationMigration @Inject constructor(
    private val migrationV40: MigrationV40,
    private val migrationV41: MigrationV41
) {

    private val migrationContext = CoroutineScope(Dispatchers.IO)

    fun migrate() {
        migrationContext.launch {
            listOf(
                migrationV40,
                migrationV41
            ).forEach { it.migrate() }
        }
    }

    interface Migration {
        suspend fun migrate()
    }
}
