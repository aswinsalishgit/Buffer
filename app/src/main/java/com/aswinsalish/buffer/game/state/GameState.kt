package com.aswinsalish.buffer.game.state

data class GameState(
    val playerBuffer: List<Int> = emptyList(),
    val botBuffer: List<Int> = emptyList(),
    val playerScore: Int = 0,
    val botScore: Int = 0,
    val roundCount: Int = 1,
    val currentPhase: TurnPhase = TurnPhase.SELECTING,
    val currentRoundPlay: RoundPlay? = null,
    val matchWinner: String? = null,
    val matchHistory: List<RoundPlay> = emptyList()
)

enum class TurnPhase {
    SELECTING,
    REVEALED,
    GAME_OVER
}

data class RoundPlay(
    val playerRightHand: Int,
    val playerLeftHandGuess: Int,
    val botRightHand: Int,
    val botLeftHandGuess: Int,
    val playerWonPoint: Boolean,
    val botWonPoint: Boolean
)

sealed class RoundResult {
    data class Success(
        val updatedState: GameState,
        val roundPlay: RoundPlay
    ) : RoundResult()
    data class Error(val message: String) : RoundResult()
}
