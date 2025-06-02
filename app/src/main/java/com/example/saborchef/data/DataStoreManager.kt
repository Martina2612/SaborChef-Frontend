package com.example.saborchef.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
// Extensión para crear DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY    = stringPreferencesKey("token")
        private val ROLE_KEY     = stringPreferencesKey("role")
        private val USER_ID_KEY  = longPreferencesKey("user_id")
        private val EMAIL_KEY    = stringPreferencesKey("email")
    }

    // Flows para leer datos
    val token: Flow<String?>    = context.dataStore.data.map { it[TOKEN_KEY] }
    val role: Flow<String?>     = context.dataStore.data.map { it[ROLE_KEY] }
    val userId: Flow<Long?>     = context.dataStore.data.map { it[USER_ID_KEY] }
    val email: Flow<String?>    = context.dataStore.data.map { it[EMAIL_KEY] }

    // Guardar sesión completa
    suspend fun saveUserData(token: String, role: String, userId: Long, email: String) {
        context.dataStore.edit {
            it[TOKEN_KEY]   = token
            it[ROLE_KEY]    = role
            it[USER_ID_KEY] = userId
            it[EMAIL_KEY]   = email
        }
        // También actualizamos el SessionManager en memoria:
        SessionManager.token = token
    }

    suspend fun clearUserData() {
        context.dataStore.edit { it.clear() }
        SessionManager.token = null
    }
}
