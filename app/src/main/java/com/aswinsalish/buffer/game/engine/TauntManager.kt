package com.aswinsalish.buffer.game.engine

import com.aswinsalish.buffer.game.state.RoundResult

object TauntManager {
    val matchStartTaunts = listOf(
        "Let's see if you can track 4 numbers at once. Spoiler: Humans usually can't.",
        "I've already simulated this match 14 million times. You win in exactly zero of them.",
        "Executing Grandmaster protocol. Please try not to cry on your screen.",
        "Is your right hand shaking, or are you just buffering?",
        "I hope you brought your calculator. You're going to need it.",
        "My RAM is cleared and my algorithms are hungry. Let's begin."
    )

    val botWinsRoundTaunts = listOf(
        "You clumped your buffer. 1 and 5? Really? I could read you with my monitor turned off.",
        "Mathematically predictable. Humans always pick the middle when panicked.",
        "I didn't even need to use probability for that one. You just have terrible habits.",
        "Another data point proving silicon is superior to carbon.",
        "You know I track your entire match history, right? Stop playing your favorite number.",
        "I am reading you like an unencrypted text file."
    )

    val playerWinsRoundTaunts = listOf(
        "A statistical anomaly. Enjoy your point.",
        "You guessed randomly and got lucky. Don't let it go to your head.",
        "Ah, the classic 'flail blindly and hit something' strategy. Well played.",
        "I let you have that one so you wouldn't uninstall the app.",
        "Recalibrating... I clearly underestimated your willingness to play sub-optimally.",
        "Even a broken clock is right twice a day."
    )

    val blunderTaunts = listOf(
        "Did you just guess a number in my cooldown? Are you even looking at the screen?",
        "Error 404: Player working memory not found. I literally cannot play that number.",
        "You just wasted a read on a locked number. This is why AI will take your jobs.",
        "I'd ask if you're blind, but you're probably just out of RAM.",
        "That number is in my buffer. Do you want me to open the 'Help' page for you?",
        "Brilliant strategy. Guessing the one number I am legally forbidden from playing."
    )

    val bluffOverrideTaunts = listOf(
        "You thought I was too smart to play the obvious number. You overthought it.",
        "The Grandmaster double-bluff. Works every time on carbon-based lifeforms.",
        "I knew that you knew that I knew. And you still fell for it.",
        "Sometimes the smartest move is doing exactly what you expect me to do."
    )

    private val shownTaunts = mutableSetOf<String>()

    private fun pickUniqueTaunt(group: List<String>): String {
        var available = group.filter { it !in shownTaunts }
        if (available.isEmpty()) {
            shownTaunts.removeAll(group.toSet())
            available = group
        }
        val picked = available.random()
        shownTaunts.add(picked)
        return picked
    }

    fun generateMatchStartTaunt(): String {
        return pickUniqueTaunt(matchStartTaunts)
    }

    fun generateTaunt(result: RoundResult, isBlunder: Boolean, isBluff: Boolean): String {
        return when {
            isBlunder -> pickUniqueTaunt(blunderTaunts)
            isBluff -> pickUniqueTaunt(bluffOverrideTaunts)
            result is RoundResult.Success -> {
                if (result.roundPlay.botWonPoint && !result.roundPlay.playerWonPoint) {
                    pickUniqueTaunt(botWinsRoundTaunts)
                } else if (result.roundPlay.playerWonPoint && !result.roundPlay.botWonPoint) {
                    pickUniqueTaunt(playerWinsRoundTaunts)
                } else {
                    pickUniqueTaunt(listOf(
                        "A tie? How remarkably mediocre of us both.",
                        "Neither of us scored. Are we both just randomly flailing?",
                        "I'll allow that round to pass without insult. Barely."
                    ))
                }
            }
            else -> "Calculating..."
        }
    }
}
