package com.example.saborchef.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.saborchef.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        private val ALIAS_KEY = stringPreferencesKey("alias")

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val ROLE_KEY = stringPreferencesKey("role") // ✅ Agregar esta línea
        private val TOKEN_KEY = stringPreferencesKey("token")

        // ✅ Nuevas keys para medios de pago
        private val NUMERO_TARJETA_KEY = stringPreferencesKey("numero_tarjeta")
        private val TIPO_TARJETA_KEY = stringPreferencesKey("tipo_tarjeta")
        private val FECHA_VENCIMIENTO_KEY = stringPreferencesKey("fecha_vencimiento")
    }

    private val dataStore = context.dataStore

    // Propiedades existentes
    val alias: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ALIAS_KEY]
    }


    val email: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }

    val userId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val role: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ROLE_KEY]
    }

    val token: Flow<String?> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // ✅ Nuevas propiedades para medios de pago
    val numeroTarjeta: Flow<String> = dataStore.data.map { preferences ->
        preferences[NUMERO_TARJETA_KEY] ?: ""
    }

    val tipoTarjeta: Flow<String> = dataStore.data.map { preferences ->
        preferences[TIPO_TARJETA_KEY] ?: "VISA"
    }

    val fechaVencimiento: Flow<String> = dataStore.data.map { preferences ->
        preferences[FECHA_VENCIMIENTO_KEY] ?: ""
    }

    // Funciones existentes
    suspend fun saveUserData(alias: String, email: String, userId: Long, role: String, token: String) {
        dataStore.edit { preferences ->
            preferences[ALIAS_KEY] = alias
            preferences[EMAIL_KEY] = email
            preferences[USER_ID_KEY] = userId
            preferences[ROLE_KEY] = role
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun saveRole(role: String) {
        dataStore.edit { preferences ->
            preferences[ROLE_KEY] = role
        }
    }

    // ✅ Nueva función para actualizar rol (para conversión a alumno)
    suspend fun updateUserRole(newRole: Rol) {
        try {
            dataStore.edit { prefs ->
                prefs[ROLE_KEY] = newRole.name
            }
            android.util.Log.d("DataStoreManager", "✅ Rol actualizado a: $newRole")
        } catch (e: Exception) {
            android.util.Log.e("DataStoreManager", "❌ Error actualizando rol: ${e.message}")
        }
    }

    // ✅ Nueva función para guardar información de tarjeta
    suspend fun saveCardInfo(numero: String, tipo: String, vencimiento: String) {
        dataStore.edit { preferences ->
            // Guardar solo los últimos 4 dígitos por seguridad
            preferences[NUMERO_TARJETA_KEY] = "**** **** **** ${numero.takeLast(4)}"
            preferences[TIPO_TARJETA_KEY] = tipo.uppercase()
            preferences[FECHA_VENCIMIENTO_KEY] = vencimiento
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}