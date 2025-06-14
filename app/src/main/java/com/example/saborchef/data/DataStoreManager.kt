package com.example.saborchef.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ROLE_KEY = stringPreferencesKey("role")
        val EMAIL_KEY = stringPreferencesKey("email")
        val USER_ID_KEY = longPreferencesKey("user_id")
    }

    suspend fun saveLoginData(token: String, role: String, email: String, userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
            prefs[EMAIL_KEY] = email
            prefs[USER_ID_KEY] = userId
        }
    }

    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val role: Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[USER_ID_KEY] }
}

