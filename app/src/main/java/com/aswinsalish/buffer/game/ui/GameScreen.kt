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
        // Top: Score & Round
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Round: ${uiState.roundCount}", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Player: ${uiState.playerScore} | Bot: ${uiState.botScore}", style = MaterialTheme.typography.titleLarge)
        }

        // Center: Game Phase Status
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            when (uiState.gamePhase) {
                GamePhase.AWAITING_INPUT -> {
                    Text("Select your hands!", style = MaterialTheme.typography.bodyLarge)
                }
                GamePhase.EXECUTING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Bot is thinking...", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                GamePhase.ROUND_OVER -> {
                    val play = uiState.currentRoundPlay
                    if (play != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("You played: Right ${play.playerRightHand}, Guessed Left ${play.playerLeftHandGuess}")
                            Text("Bot played: Right ${play.botRightHand}, Guessed Left ${play.botLeftHandGuess}")
                            Spacer(modifier = Modifier.height(8.dp))
                            if (play.playerWonPoint) Text("You won a point!", color = MaterialTheme.colorScheme.primary)
                            if (play.botWonPoint) Text("Bot won a point!", color = MaterialTheme.colorScheme.error)
                            if (!play.playerWonPoint && !play.botWonPoint) Text("No points this round.")
                        }
                    }
                }
                GamePhase.GAME_OVER -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Game Over!", style = MaterialTheme.typography.headlineLarge)
                        Text("Winner: ${uiState.matchWinner}", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.resetGame() }) {
                            Text("Play Again")
                        }
                    }
                }
            }
        }

        // Bottom: Inputs
        if (uiState.gamePhase == GamePhase.AWAITING_INPUT) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Right Hand (Play 1-5)", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { num ->
                        val isOnCooldown = uiState.playerBuffer.contains(num)
                        Button(
                            onClick = { selectedRightHand = num },
                            enabled = !isOnCooldown,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedRightHand == num) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(num.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Left Hand (Guess 1-5)", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..5).forEach { num ->
                        Button(
                            onClick = { selectedLeftHand = num },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedLeftHand == num) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Text(num.toString())
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (selectedRightHand != null && selectedLeftHand != null) {
                            viewModel.executeTurn(selectedRightHand!!, selectedLeftHand!!)
                            selectedRightHand = null
                            selectedLeftHand = null
                        }
                    },
                    enabled = selectedRightHand != null && selectedLeftHand != null
                ) {
                    Text("Execute Turn")
                }
            }
        }
    }
}
