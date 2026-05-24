package com.aswinsalish.buffer.game.state

data class GameState(
    val playerBuffer: List<Int> = emptyList(),
    val botBuffer: List<Int> = emptyList(),
    val playerScore: Int = 0,
    val botScore: Int = 0,
    val roundCount: Int = 1,
    val gamePhase: GamePhase = GamePhase.AWAITING_INPUT,
    val currentRoundPlay: RoundPlay? = null,
    val matchWinner: String? = null
)

enum class GamePhase {
    AWAITING_INPUT,
    EXECUTING,
    ROUND_OVER,
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
