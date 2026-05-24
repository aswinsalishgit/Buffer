package com.aswinsalish.buffer.game.viewmodel

import androidx.lifecycle.ViewModel
import com.aswinsalish.buffer.game.state.TurnPhase
import com.aswinsalish.buffer.game.state.GameState
import com.aswinsalish.buffer.game.state.RoundPlay
import com.aswinsalish.buffer.game.state.RoundResult
import com.aswinsalish.buffer.game.state.BotDifficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random
import com.aswinsalish.buffer.game.engine.TauntManager

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private fun processRound(playerRight: Int, playerLeft: Int): RoundResult {
        val currentState = _uiState.value

        // Validate playerRight against playerBuffer
        if (currentState.playerBuffer.contains(playerRight)) {
            return RoundResult.Error("Invalid move: Right hand is on cooldown.")
        }

        val (botRight, botLeft, isBluff) = calculateBotMoves(currentState, currentState.botDifficulty)
        
        val isBlunder = currentState.botBuffer.contains(playerLeft)

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
        
        // Track the player's every single move and read
        val newPlayerStateHistory = currentState.playerStateHistory + playerRight
        val newPlayerReadHistory = currentState.playerReadHistory + playerLeft

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
            matchHistory = newMatchHistory,
            playerStateHistory = newPlayerStateHistory,
            playerReadHistory = newPlayerReadHistory
        )

        val successResult = RoundResult.Success(updatedState, roundPlay)
        val taunt = TauntManager.generateTaunt(successResult, isBlunder, isBluff)
        val finalState = updatedState.copy(currentBotMessage = taunt)

        // Return a RoundResult object containing the outcome and updated state.
        return RoundResult.Success(finalState, roundPlay)
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

    fun resetGame(selectedDifficulty: BotDifficulty = BotDifficulty.MEDIUM) {
        _uiState.value = GameState(
            botDifficulty = selectedDifficulty,
            currentBotMessage = TauntManager.matchStartTaunts.random()
        )
    }

    private fun calculateBotMoves(currentState: GameState, difficulty: BotDifficulty): Triple<Int, Int, Boolean> {
        val validBotRights = (1..5).filter { !currentState.botBuffer.contains(it) }
        val playerAvailableMoves = (1..5).filter { !currentState.playerBuffer.contains(it) }

        var botLeft: Int
        var botRight: Int
        var isBluff = false

        when (difficulty) {
            BotDifficulty.EASY -> {
                // EASY: The Grunt
                botLeft = Random.nextInt(1, 6)
                botRight = validBotRights.randomOrNull() ?: 1
            }
            BotDifficulty.MEDIUM -> {
                // MEDIUM: The Sniper
                botLeft = if (playerAvailableMoves.isNotEmpty()) playerAvailableMoves.random() else Random.nextInt(1, 6)
                
                val lastPlayerGuess = currentState.playerReadHistory.lastOrNull()
                val safeBotRights = if (lastPlayerGuess != null && validBotRights.contains(lastPlayerGuess)) {
                    validBotRights.filter { it != lastPlayerGuess }
                } else {
                    validBotRights
                }
                
                botRight = if (safeBotRights.isNotEmpty()) safeBotRights.random() else validBotRights.randomOrNull() ?: 1
            }
            BotDifficulty.HARD -> {
                // HARD: The Grandmaster
                
                // botLeft (Offense): Find the number from available moves that the player has played MOST frequently
                if (playerAvailableMoves.isNotEmpty()) {
                    val frequencyMap = currentState.playerStateHistory.groupingBy { it }.eachCount()
                    // Filter frequencies to only include currently available moves
                    val availableFrequencies = playerAvailableMoves.associateWith { frequencyMap[it] ?: 0 }
                    val maxFreq = availableFrequencies.values.maxOrNull()
                    val mostFrequentMoves = availableFrequencies.filter { it.value == maxFreq }.keys.toList()
                    botLeft = mostFrequentMoves.randomOrNull() ?: playerAvailableMoves.random()
                } else {
                    botLeft = Random.nextInt(1, 6)
                }

                // botRight (Defense): Find the number from valid bot moves that the player guesses LEAST frequently
                val readFrequencyMap = currentState.playerReadHistory.groupingBy { it }.eachCount()
                val availableReadFrequencies = validBotRights.associateWith { readFrequencyMap[it] ?: 0 }
                
                val minFreq = availableReadFrequencies.values.minOrNull()
                val leastGuessedMoves = availableReadFrequencies.filter { it.value == minFreq }.keys.toList()
                val standardBotRight = leastGuessedMoves.randomOrNull() ?: validBotRights.randomOrNull() ?: 1
                
                // The Bluff Override: 20% chance to invert logic
                if (Random.nextFloat() < 0.2f) {
                    isBluff = true
                    val maxReadFreq = availableReadFrequencies.values.maxOrNull()
                    val mostGuessedMoves = availableReadFrequencies.filter { it.value == maxReadFreq }.keys.toList()
                    botRight = mostGuessedMoves.randomOrNull() ?: validBotRights.randomOrNull() ?: 1
                } else {
                    botRight = standardBotRight
                }
            }
        }
        
        return Triple(botRight, botLeft, isBluff)
    }
}
