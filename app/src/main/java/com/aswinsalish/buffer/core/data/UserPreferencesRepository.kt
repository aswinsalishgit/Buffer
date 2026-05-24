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
import com.aswinsalish.buffer.game.state.BotDifficulty

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val username: String?,
    val termsAccepted: Boolean,
    val defaultDifficulty: BotDifficulty,
    val botInteractionsEnabled: Boolean
)

class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    companion object {
        val USERNAME = stringPreferencesKey("username")
        val TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
        val DEFAULT_DIFFICULTY = stringPreferencesKey("default_difficulty")
        val BOT_INTERACTIONS_ENABLED = booleanPreferencesKey("bot_interactions_enabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .map { preferences ->
            val username = preferences[USERNAME]
            val termsAccepted = preferences[TERMS_ACCEPTED] ?: false
            val difficultyStr = preferences[DEFAULT_DIFFICULTY]
            val difficulty = try {
                if (difficultyStr != null) BotDifficulty.valueOf(difficultyStr) else BotDifficulty.MEDIUM
            } catch (e: Exception) {
                BotDifficulty.MEDIUM
            }
            val botInteractionsEnabled = preferences[BOT_INTERACTIONS_ENABLED] ?: true
            UserPreferences(username, termsAccepted, difficulty, botInteractionsEnabled)
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

    suspend fun saveDefaultDifficulty(difficulty: BotDifficulty) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_DIFFICULTY] = difficulty.name
        }
    }

    suspend fun saveBotInteractionsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BOT_INTERACTIONS_ENABLED] = enabled
        }
    }
}
