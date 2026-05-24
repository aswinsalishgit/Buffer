package com.aswinsalish.buffer.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aswinsalish.buffer.game.state.GamePhase
import com.aswinsalish.buffer.game.state.GameState
import com.aswinsalish.buffer.game.state.RoundPlay
import com.aswinsalish.buffer.game.state.RoundResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun processRound(playerRight: Int, playerLeft: Int): RoundResult {
        val currentState = _uiState.value

        // Validate playerRight against playerBuffer
        if (currentState.playerBuffer.contains(playerRight)) {
            return RoundResult.Error("Invalid move: Right hand is on cooldown.")
        }

        // Bot Right Hand (Defensive)
        val validBotRights = (1..5).filter { !currentState.botBuffer.contains(it) }
        
        // Track the player's last 3 moves and avoid numbers the player has guessed correctly before
        val recentHistory = currentState.matchHistory.takeLast(3)
        val playerSuccessfulGuesses = recentHistory.filter { it.playerWonPoint }.map { it.playerLeftHandGuess }.toSet()
        val safeBotRights = validBotRights.filter { it !in playerSuccessfulGuesses }
        
        val botRight = if (safeBotRights.isNotEmpty()) safeBotRights.random() else validBotRights.randomOrNull() ?: 1

        // Bot Left Hand (Offensive)
        // Analyze playerBuffer to see which numbers are locked. Prioritize guessing available numbers.
        val availablePlayerHands = (1..5).filter { !currentState.playerBuffer.contains(it) }
        val botLeft = if (availablePlayerHands.isNotEmpty()) availablePlayerHands.random() else (1..5).random()

        // Calculate the winner of the round
        val playerWonPoint = playerLeft == botRight
        val botWonPoint = botLeft == playerRight

        val roundPlay = RoundPlay(
            playerRightHand = playerRight,
            playerLeftHandGuess = playerLeft,
            botRightHand = botRight,
            botLeftHandGuess = botLeft,
            playerWonPoint = playerWonPoint,
            botWonPoint = botWonPoint
        )

        // Update the buffers for both players (maintaining the 2-round cooldown)
        val newPlayerBuffer = (currentState.playerBuffer + listOf(playerRight)).takeLast(2)
        val newBotBuffer = (currentState.botBuffer + listOf(botRight)).takeLast(2)

        // Update match history
        val newMatchHistory = currentState.matchHistory + roundPlay

        // Update scores
        val newPlayerScore = currentState.playerScore + if (playerWonPoint) 1 else 0
        val newBotScore = currentState.botScore + if (botWonPoint) 1 else 0

        // Create updated state
        val updatedState = currentState.copy(
            playerBuffer = newPlayerBuffer,
            botBuffer = newBotBuffer,
            playerScore = newPlayerScore,
            botScore = newBotScore,
            currentRoundPlay = roundPlay,
            matchHistory = newMatchHistory
        )

        // Return a RoundResult object containing the outcome and updated state.
        return RoundResult.Success(updatedState, roundPlay)
    }

    fun executeTurn(playerRightHand: Int, playerLeftHandGuess: Int) {
        if (_uiState.value.gamePhase != GamePhase.AWAITING_INPUT) return

        val result = processRound(playerRightHand, playerLeftHandGuess)
        if (result is RoundResult.Error) {
            // Log or expose the error to the UI. For now, simply return.
            return
        }

        val successResult = result as RoundResult.Success

        _uiState.update { it.copy(gamePhase = GamePhase.EXECUTING) }

        viewModelScope.launch {
            // Simulate bot thinking
            delay(1000)

            // Apply the processed updated state to the UI Flow
            _uiState.value = successResult.updatedState

            delay(1000) // Time to show the round results

            val newPlayerScore = _uiState.value.playerScore
            val newBotScore = _uiState.value.botScore

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
