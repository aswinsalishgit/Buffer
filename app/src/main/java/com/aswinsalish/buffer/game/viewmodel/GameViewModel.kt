package com.aswinsalish.buffer.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aswinsalish.buffer.game.logic.BotEngine
import com.aswinsalish.buffer.game.state.GamePhase
import com.aswinsalish.buffer.game.state.GameState
import com.aswinsalish.buffer.game.state.RoundPlay
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private val botEngine = BotEngine()

    fun executeTurn(playerRightHand: Int, playerLeftHandGuess: Int) {
        if (_uiState.value.gamePhase != GamePhase.AWAITING_INPUT) return

        _uiState.update { it.copy(gamePhase = GamePhase.EXECUTING) }

        viewModelScope.launch {
            // Simulate bot thinking
            delay(1000)

            val (botRightHand, botLeftHandGuess) = botEngine.generateMove(_uiState.value.botBuffer)

            val playerWonPoint = playerLeftHandGuess == botRightHand
            val botWonPoint = botLeftHandGuess == playerRightHand

            val roundPlay = RoundPlay(
                playerRightHand = playerRightHand,
                playerLeftHandGuess = playerLeftHandGuess,
                botRightHand = botRightHand,
                botLeftHandGuess = botLeftHandGuess,
                playerWonPoint = playerWonPoint,
                botWonPoint = botWonPoint
            )

            // Update scores
            val newPlayerScore = _uiState.value.playerScore + if (playerWonPoint) 1 else 0
            val newBotScore = _uiState.value.botScore + if (botWonPoint) 1 else 0

            // Update buffers
            val newPlayerBuffer = (_uiState.value.playerBuffer + listOf(playerRightHand)).takeLast(2)
            val newBotBuffer = (_uiState.value.botBuffer + listOf(botRightHand)).takeLast(2)

            _uiState.update {
                it.copy(
                    playerBuffer = newPlayerBuffer,
                    botBuffer = newBotBuffer,
                    playerScore = newPlayerScore,
                    botScore = newBotScore,
                    currentRoundPlay = roundPlay
                )
            }

            delay(1000) // Time to show the round results

            if (newPlayerScore >= 3 || newBotScore >= 3) {
                val winner = if (newPlayerScore >= 3 && newBotScore >= 3) "Tie" 
                             else if (newPlayerScore >= 3) "Player" 
                             else "Bot"
                _uiState.update { 
                    it.copy(gamePhase = GamePhase.GAME_OVER, matchWinner = winner) 
                }
            } else {
                _uiState.update { 
                    it.copy(gamePhase = GamePhase.ROUND_OVER) 
                }
                delay(2000)
                _uiState.update {
                    it.copy(
                        gamePhase = GamePhase.AWAITING_INPUT,
                        roundCount = it.roundCount + 1,
                        currentRoundPlay = null
                    )
                }
            }
        }
    }

    fun resetGame() {
        _uiState.value = GameState()
    }
}
