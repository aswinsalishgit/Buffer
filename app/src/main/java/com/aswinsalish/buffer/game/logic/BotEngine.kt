package com.aswinsalish.buffer.game.logic

class BotEngine {
    fun generateMove(botBuffer: List<Int>): Pair<Int, Int> {
        val availableHands = (1..5).filter { it !in botBuffer }
        val rightHand = availableHands.randomOrNull() ?: 1
        val leftHandGuess = (1..5).random()
        return Pair(rightHand, leftHandGuess)
    }
}
