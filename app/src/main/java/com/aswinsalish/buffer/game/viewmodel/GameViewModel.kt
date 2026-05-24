package com.aswinsalish.buffer.game.viewmodel

import androidx.lifecycle.ViewModel
import com.aswinsalish.buffer.game.state.TurnPhase
import com.aswinsalish.buffer.game.state.GameState
import com.aswinsalish.buffer.game.state.RoundPlay
import com.aswinsalish.buffer.game.state.RoundResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private fun processRound(playerRight: Int, playerLeft: Int): RoundResult {
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
        if (_uiState.value.currentPhase != TurnPhase.SELECTING) return

        val result = processRound(playerRightHand, playerLeftHandGuess)
        if (result is RoundResult.Error) {
            return
        }

        val successResult = result as RoundResult.Success
        val newState = successResult.updatedState
        
        if (newState.playerScore >= 3 || newState.botScore >= 3) {
            val winner = if (newState.playerScore >= 3 && newState.botScore >= 3) "Tie" 
                         else if (newState.playerScore >= 3) "Player" 
                         else "Bot"
            _uiState.value = newState.copy(currentPhase = TurnPhase.GAME_OVER, matchWinner = winner)
        } else {
            // Update the state with the bot's moves and the round winner, but also change currentPhase to TurnPhase.REVEALED
            _uiState.value = newState.copy(currentPhase = TurnPhase.REVEALED)
        }
    }

    fun startNextRound() {
        if (_uiState.value.currentPhase == TurnPhase.REVEALED) {
            _uiState.update {
                it.copy(
                    currentPhase = TurnPhase.SELECTING,
                    roundCount = it.roundCount + 1,
                    currentRoundPlay = null
                )
            }
        }
    }

    fun resetGame() {
        _uiState.value = GameState()
    }
}
