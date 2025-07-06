package com.example.saborchef.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.saborchef.model.Rol
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ROLE_KEY = stringPreferencesKey("role")
        val EMAIL_KEY = stringPreferencesKey("email")
        val USER_ID_KEY = longPreferencesKey("user_id")
        val ALIAS_KEY = stringPreferencesKey("alias")
    }

    // Flows para leer datos
    val token: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val role: Flow<String?> = context.dataStore.data.map { it[ROLE_KEY] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[USER_ID_KEY] }
    val alias: Flow<String?> = context.dataStore.data.map { it[ALIAS_KEY] }

    // Guardar datos de login
    suspend fun saveLoginData(token: String, role: String, email: String, userId: Long, alias: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
            prefs[EMAIL_KEY] = email
            prefs[USER_ID_KEY] = userId
            prefs[ALIAS_KEY] = alias
        }
    }

    // Guardar sesión completa
    suspend fun saveUserData(token: String, role: String, userId: Long?, email: String, alias: String?) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
            userId?.let { prefs[USER_ID_KEY] = it }
            prefs[EMAIL_KEY] = email
            alias?.let { prefs[ALIAS_KEY] = it }
        }
    }

    // Guardar solo el rol
    suspend fun saveRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[ROLE_KEY] = role
        }
    }

    // ✅ NUEVA FUNCIÓN: Actualizar rol de usuario (para conversión a alumno)
    suspend fun updateUserRole(newRole: Rol) {
        try {
            context.dataStore.edit { prefs ->
                prefs[ROLE_KEY] = newRole.name
            }
            Log.d("DataStoreManager", "✅ Rol actualizado a: $newRole")
        } catch (e: Exception) {
            Log.e("DataStoreManager", "❌ Error actualizando rol: ${e.message}", e)
        }
    }

    // Limpiar todos los datos
    suspend fun clearUserData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
        // Si tienes SessionManager, descomenta la siguiente línea
        // SessionManager.token = null
    }
}