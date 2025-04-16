package com.example.autentikasiexternal.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

object UserKeys {
    val NAME = stringPreferencesKey("user_name")
    val EMAIL = stringPreferencesKey("user_email")
}

class UserPreferences(private val context: Context) {
    suspend fun saveUser(name: String, email: String) {
        context.dataStore.edit {
            it[UserKeys.NAME] = name
            it[UserKeys.EMAIL] = email
        }
    }

    val userName = context.dataStore.data.map { it[UserKeys.NAME] ?: "" }
    val userEmail = context.dataStore.data.map { it[UserKeys.EMAIL] ?: "" }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}