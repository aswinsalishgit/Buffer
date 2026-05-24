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

        // Center Area dynamically adapts based on phase
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            when (uiState.currentPhase) {
                TurnPhase.SELECTING -> {
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
                                    onClick = { selectedRightHand = num }
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
                                    isOnCooldown = false, // Left hand doesn't have a cooldown in standard rules
                                    onClick = { selectedLeftHand = num }
                                )
                            }
                        }
                    }
                }
                TurnPhase.REVEALED -> {
                    val play = uiState.currentRoundPlay
                    if (play != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            StackHeader("COMBAT LOG")
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray)
                                    .padding(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text("YOUR RIGHT HAND VS BOT'S GUESS", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("You Played: ${play.playerRightHand}  |  Bot Guessed: ${play.botLeftHandGuess}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (play.botWonPoint) Text("BOT WON A POINT", color = MaterialTheme.colorScheme.error)
                                    else Text("BOT MISSED", color = AccentColor)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.Gray)
                                    .padding(16.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                    Text("YOUR GUESS VS BOT'S RIGHT HAND", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("You Guessed: ${play.playerLeftHandGuess}  |  Bot Played: ${play.botRightHand}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (play.playerWonPoint) Text("YOU WON A POINT", color = AccentColor)
                                    else Text("YOU MISSED", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                TurnPhase.GAME_OVER -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StackHeader("MATCH CONCLUDED")
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "WINNER: ${uiState.matchWinner?.uppercase()}", 
                            style = MaterialTheme.typography.headlineLarge,
                            color = AccentColor
                        )
                    }
                }
            }
        }

        // Bottom Action Panel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (uiState.currentPhase) {
                TurnPhase.SELECTING -> {
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
                }
                TurnPhase.REVEALED -> {
                    BlockyButton(
                        text = "NEXT ROUND",
                        onClick = {
                            selectedRightHand = null
                            selectedLeftHand = null
                            viewModel.startNextRound()
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                }
                TurnPhase.GAME_OVER -> {
                    BlockyButton(
                        text = "PLAY AGAIN",
                        onClick = {
                            selectedRightHand = null
                            selectedLeftHand = null
                            viewModel.resetGame()
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp)
                    )
                }
            }
        }
    }
}
