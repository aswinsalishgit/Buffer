package com.aswinsalish.buffer.core.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val username: String?,
    val termsAccepted: Boolean
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            val username = preferences[USERNAME]
            val termsAccepted = preferences[TERMS_ACCEPTED] ?: false
            UserPreferences(username, termsAccepted)
        }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = username
        }
    }

    suspend fun saveTermsAccepted(accepted: Boolean) {
        dataStore.edit { preferences ->
            preferences[TERMS_ACCEPTED] = accepted
        }
    }
}
