package quickbeer.android.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class LegacyPreferences(context: Context) {

    private val pref = createPreferences(context)

    var userId: Int?
        get() = getInt(USERID)
        set(value) = setInt(USERID, value)

    var username: String?
        get() = getString(USERNAME)
        set(value) = setString(USERNAME, value)

    var password: String?
        get() = getString(PASSWORD)
        set(value) = setString(PASSWORD, value)

    private fun getString(key: String): String? {
        return pref.getString(key, null)
    }

    private fun setString(key: String, value: String?) {
        if (value != null) pref.edit { putString(key, value) }
        else pref.edit { remove(key) }
    }

    private fun getInt(key: String): Int? {
        return pref.getInt(key, Int.MIN_VALUE)
            .takeIf { it != Int.MIN_VALUE }
    }

    private fun setInt(key: String, value: Int?) {
        if (value != null) pref.edit { putInt(key, value) }
        else pref.edit { remove(key) }
    }

    companion object {
        private const val USERID = "PREF_USERID"
        private const val USERNAME = "PREF_USERNAME"
        private const val PASSWORD = "PREF_PASSWORD"

        private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        fun createPreferences(context: Context): SharedPreferences {
            return EncryptedSharedPreferences.create(
                "quickbeer_preferences",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}
