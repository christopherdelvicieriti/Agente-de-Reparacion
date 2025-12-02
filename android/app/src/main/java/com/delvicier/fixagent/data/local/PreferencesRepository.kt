package com.delvicier.fixagent.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {

    private object PreferenceKeys {
        val BASE_URL = stringPreferencesKey("base_url")

        val TOKEN_ACCOUNT = stringPreferencesKey("token_account")

        val SECRET_KEY = stringPreferencesKey("secret_key")

        val PENDING_SECRET_BACKUP = booleanPreferencesKey("pending_secret_backup")

        val TOKEN = stringPreferencesKey("token")
    }

    val baseUrl: Flow<String?> = context.dataStore.data.map { it[PreferenceKeys.BASE_URL] }
    val tokenAccount: Flow<String?> = context.dataStore.data.map { it[PreferenceKeys.TOKEN_ACCOUNT] }
    val token: Flow<String?> = context.dataStore.data.map { it[PreferenceKeys.TOKEN] }

    val isBackupPending: Flow<Boolean> = context.dataStore.data.map {
        it[PreferenceKeys.PENDING_SECRET_BACKUP] ?: false
    }

    suspend fun saveBaseUrl(url: String) {
        context.dataStore.edit { it[PreferenceKeys.BASE_URL] = url }
    }

    suspend fun saveTokenAccount(token: String) {
        context.dataStore.edit { it[PreferenceKeys.TOKEN_ACCOUNT] = token }
    }

    suspend fun clearTokenAccount() {
        context.dataStore.edit { it.remove(PreferenceKeys.TOKEN_ACCOUNT) }
    }

    suspend fun saveSecretKey(secret: String) {
        context.dataStore.edit {
            it[PreferenceKeys.SECRET_KEY] = secret
            it[PreferenceKeys.PENDING_SECRET_BACKUP] = true
        }
    }

    suspend fun confirmBackupDone() {
        context.dataStore.edit {
            it[PreferenceKeys.PENDING_SECRET_BACKUP] = false
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[PreferenceKeys.TOKEN] = token }
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(PreferenceKeys.TOKEN) }
    }
}