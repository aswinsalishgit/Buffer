package com.aswinsalish.buffer.core.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.aswinsalish.buffer.game.state.BotDifficulty

sealed class PreferencesState {
    object Loading : PreferencesState()
    data class Loaded(val prefs: UserPreferences) : PreferencesState()
}

class UserPreferencesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = UserPreferencesRepository(application.dataStore)

    val preferencesState: StateFlow<PreferencesState> = repository.userPreferencesFlow
        .map { PreferencesState.Loaded(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PreferencesState.Loading
        )

    fun completeOnboarding(username: String) {
        viewModelScope.launch {
            repository.saveUsername(username)
            repository.saveTermsAccepted(true)
        }
    }

    fun saveDefaultDifficulty(difficulty: BotDifficulty) {
        viewModelScope.launch {
            repository.saveDefaultDifficulty(difficulty)
        }
    }
}
