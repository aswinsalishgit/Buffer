package com.aswinsalish.buffer.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.core.components.BlockyButton
import com.aswinsalish.buffer.core.components.LoadoutSquareButton
import com.aswinsalish.buffer.core.components.StackHeader
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.game.state.TurnPhase
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
        // Top Information Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO: Help */ }) {
                Icon(Icons.Default.Info, contentDescription = "Help", tint = AccentColor)
            }
            Text(
                text = "ROUND ${uiState.roundCount} | YOUR SCORE ${uiState.playerScore} | BOT SCORE ${uiState.botScore}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { /* TODO: Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AccentColor)
            }
        }

        // Center Area (Grid remains persistent in background)
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                StackHeader("YOUR MOVE (RIGHT HAND)")
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    (1..5).forEach { num ->
                        val isOnCooldown = uiState.playerBuffer.contains(num)
                        val isSelected = selectedRightHand == num
                        LoadoutSquareButton(
                            number = num,
                            isSelected = isSelected,
                            isOnCooldown = isOnCooldown,
                            onClick = { if (uiState.currentPhase == TurnPhase.SELECTING) selectedRightHand = num }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                StackHeader("YOUR READ (LEFT HAND)")
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    (1..5).forEach { num ->
                        val isSelected = selectedLeftHand == num
                        LoadoutSquareButton(
                            number = num,
                            isSelected = isSelected,
                            isOnCooldown = false,
                            onClick = { if (uiState.currentPhase == TurnPhase.SELECTING) selectedLeftHand = num }
                        )
                    }
                }
            }

            // Modal Overlays
            if (uiState.currentPhase == TurnPhase.REVEALED) {
                val play = uiState.currentRoundPlay
                if (play != null) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { }) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            border = BorderStroke(2.dp, Color.LightGray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val resultText = when {
                                    play.playerWonPoint && play.botWonPoint -> "DRAW"
                                    play.playerWonPoint -> "YOU WIN!"
                                    play.botWonPoint -> "BOT WINS"
                                    else -> "NO POINTS"
                                }
                                val resultColor = if (play.playerWonPoint && !play.botWonPoint) AccentColor else if (play.botWonPoint) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                
                                Text("ROUND RESULT:", style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
                                Text(resultText, style = MaterialTheme.typography.headlineMedium, color = resultColor)
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                
                                // Column A
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("YOUR STATE: ${play.playerRightHand} vs BOT READ: ${play.botLeftHandGuess}", 
                                         style = MaterialTheme.typography.bodyLarge, 
                                         color = if (!play.botWonPoint) AccentColor else Color.Gray)
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Column B
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("YOUR READ: ${play.playerLeftHandGuess} vs BOT STATE: ${play.botRightHand}", 
                                         style = MaterialTheme.typography.bodyLarge, 
                                         color = if (play.playerWonPoint) AccentColor else Color.Gray)
                                }
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                BlockyButton(
                                    text = "NEXT ROUND",
                                    onClick = {
                                        selectedRightHand = null
                                        selectedLeftHand = null
                                        viewModel.startNextRound()
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp)
                                )
                            }
                        }
                    }
                }
            } else if (uiState.currentPhase == TurnPhase.GAME_OVER) {
                androidx.compose.ui.window.Dialog(onDismissRequest = { }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        border = BorderStroke(2.dp, AccentColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("MATCH CONCLUDED", style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("WINNER: ${uiState.matchWinner?.uppercase()}", style = MaterialTheme.typography.headlineLarge, color = AccentColor)
                            Spacer(modifier = Modifier.height(32.dp))
                            BlockyButton(
                                text = "PLAY AGAIN",
                                onClick = {
                                    selectedRightHand = null
                                    selectedLeftHand = null
                                    viewModel.resetGame()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Panel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.currentPhase == TurnPhase.SELECTING) {
                BlockyButton(
                    text = "EXECUTE",
                    onClick = {
                        if (selectedRightHand != null && selectedLeftHand != null) {
                            viewModel.executeTurn(selectedRightHand!!, selectedLeftHand!!)
                        }
                    },
                    enabled = selectedRightHand != null && selectedLeftHand != null,
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}
