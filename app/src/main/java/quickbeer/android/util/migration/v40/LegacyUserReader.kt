package quickbeer.android.util.migration.v40

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object LegacyUserReader {

    fun readUser(db: SQLiteDatabase, moshi: Moshi): LegacyUser? {
        val cursor = db.rawQuery("SELECT json FROM users", null)
        val values = generateSequence { if (cursor.moveToNext()) cursor else null }
            .mapNotNull { readUserFromCursor(it, moshi) }
            .toList()

        cursor.close()

        return values.firstOrNull()
    }

    @SuppressLint("Range")
    private fun readUserFromCursor(cursor: Cursor, moshi: Moshi): LegacyUser? {
        val json = cursor.getString(cursor.getColumnIndex("json"))
        val type = Types.newParameterizedType(LegacyUser::class.java)
        val adapter = moshi.adapter<LegacyUser>(type)

        return adapter.fromJson(json)
    }
}
