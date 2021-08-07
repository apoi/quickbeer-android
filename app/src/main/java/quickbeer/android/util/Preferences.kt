package quickbeer.android.util

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class Preferences(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val pref = EncryptedSharedPreferences.create(
        "quickbeer_preferences",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

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
        return pref.edit { putString(key, value) }
    }

    companion object {
        private const val USERNAME = "PREF_USERNAME"
        private const val PASSWORD = "PREF_PASSWORD"
    }
}
