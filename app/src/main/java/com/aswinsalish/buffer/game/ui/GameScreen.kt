package com.aswinsalish.buffer.game.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.game.state.GamePhase
import com.aswinsalish.buffer.game.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedRightHand by remember { mutableStateOf<Int?>(null) }
    var selectedLeftHand by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Score & Bot Status
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Round: ${uiState.roundCount}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Player: ${uiState.playerScore}  |  Bot: ${uiState.botScore}", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val validBotMoves = (1..5).filter { it !in uiState.botBuffer }
                    Text(
                        "Bot Valid Moves: ${validBotMoves.joinToString()}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (uiState.botBuffer.isNotEmpty()) {
                        Text(
                            "Bot Cooldown: ${uiState.botBuffer.joinToString()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Center: Reveal Animation (Placeholder)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (uiState.gamePhase) {
                GamePhase.AWAITING_INPUT -> {
                    Text("Select your hands to begin the round!", style = MaterialTheme.typography.bodyLarge)
                }
                GamePhase.EXECUTING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Revealing moves...", style = MaterialTheme.typography.headlineSmall)
                    }
                }
                GamePhase.ROUND_OVER -> {
                    val play = uiState.currentRoundPlay
                    if (play != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("You played: Right ${play.playerRightHand}, Guessed Left ${play.playerLeftHandGuess}", style = MaterialTheme.typography.bodyLarge)
                            Text("Bot played: Right ${play.botRightHand}, Guessed Left ${play.botLeftHandGuess}", style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            if (play.playerWonPoint) Text("You won a point!", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
                            if (play.botWonPoint) Text("Bot won a point!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                            if (!play.playerWonPoint && !play.botWonPoint) Text("No points this round.", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
                GamePhase.GAME_OVER -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
                        Text("Winner: ${uiState.matchWinner}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.resetGame() },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Play Again", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        // Bottom: Control Panel
        if (uiState.gamePhase == GamePhase.AWAITING_INPUT) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Row 1: Right Hand
                Text("Right Hand (Play 1-5)", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    (1..5).forEach { num ->
                        val isOnCooldown = uiState.playerBuffer.contains(num)
                        Button(
                            onClick = { selectedRightHand = num },
                            enabled = !isOnCooldown,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRightHand == num) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.size(56.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(num.toString(), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Row 2: Left Hand
                Text("Left Hand (Guess 1-5)", style = MaterialTheme.typography.titleMedium)
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    (1..5).forEach { num ->
                        Button(
                            onClick = { selectedLeftHand = num },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLeftHand == num) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.size(56.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(num.toString(), style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Big Execute Button
                Button(
                    onClick = {
                        if (selectedRightHand != null && selectedLeftHand != null) {
                            viewModel.executeTurn(selectedRightHand!!, selectedLeftHand!!)
                            selectedRightHand = null
                            selectedLeftHand = null
                        }
                    },
                    enabled = selectedRightHand != null && selectedLeftHand != null,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Text("Execute Turn", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
    }
}
