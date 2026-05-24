package com.aswinsalish.buffer.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.core.components.TacticalButton
import com.aswinsalish.buffer.core.components.BlockyTextField
import com.aswinsalish.buffer.core.components.StackHeader
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor
import com.aswinsalish.buffer.game.state.TurnPhase
import com.aswinsalish.buffer.game.viewmodel.GameViewModel

@Composable
fun GameScreen(
    gameViewModel: GameViewModel = viewModel(),
    prefsViewModel: UserPreferencesViewModel = viewModel()
) {
    val uiState by gameViewModel.uiState.collectAsState()
    
    var selectedRightHand by remember { mutableStateOf<Int?>(null) }
    var selectedLeftHand by remember { mutableStateOf<Int?>(null) }

    var showHelpDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
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
            IconButton(onClick = { showHelpDialog = true }) {
                Icon(Icons.Default.Info, contentDescription = "Help", tint = AccentColor)
            }
            Text(
                text = "ROUND ${uiState.roundCount} | YOUR SCORE ${uiState.playerScore} | BOT SCORE ${uiState.botScore}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showSettingsDialog = true }) {
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
                        TacticalButton(
                            text = num.toString(),
                            isActive = isSelected,
                            isDisabled = isOnCooldown,
                            onClick = { if (uiState.currentPhase == TurnPhase.SELECTING) selectedRightHand = num },
                            modifier = Modifier.size(56.dp)
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
                        TacticalButton(
                            text = num.toString(),
                            isActive = isSelected,
                            isDisabled = false,
                            onClick = { if (uiState.currentPhase == TurnPhase.SELECTING) selectedLeftHand = num },
                            modifier = Modifier.size(56.dp)
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
                            colors = CardDefaults.cardColors(containerColor = BackgroundColor),
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
                                
                                TacticalButton(
                                    text = "NEXT ROUND",
                                    onClick = {
                                        selectedRightHand = null
                                        selectedLeftHand = null
                                        gameViewModel.startNextRound()
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
                        colors = CardDefaults.cardColors(containerColor = BackgroundColor),
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
                            TacticalButton(
                                text = "PLAY AGAIN",
                                onClick = {
                                    selectedRightHand = null
                                    selectedLeftHand = null
                                    gameViewModel.resetGame()
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }
            }

            // Auxiliary Popups
            if (showHelpDialog) {
                HelpDialog(onDismiss = { showHelpDialog = false })
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    viewModel = prefsViewModel,
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }

        // Bottom Action Panel
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.currentPhase == TurnPhase.SELECTING) {
                TacticalButton(
                    text = "EXECUTE",
                    onClick = {
                        if (selectedRightHand != null && selectedLeftHand != null) {
                            gameViewModel.executeTurn(selectedRightHand!!, selectedLeftHand!!)
                        }
                    },
                    isDisabled = !(selectedRightHand != null && selectedLeftHand != null),
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundColor),
            border = BorderStroke(2.dp, Color.LightGray),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "DATABASE ARCHIVE",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AccentColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    StackHeader("SECTION 1: HOW TO PLAY")
                    Text(
                        text = "Buffer is a 1v1 psychological combat simulation. Each round, you must simultaneously lock in a MOVE (Right Hand) and a READ (Left Hand) by selecting numbers from 1 to 5. Your MOVE dictates your attack posture, while your READ is an attempt to predict the opponent's MOVE.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    StackHeader("SECTION 2: GAME RULES")
                    Text(
                        text = "If your READ matches the opponent's MOVE exactly, you score a point. If both players successfully read each other, it's a DRAW and both players score a point. If neither reads the opponent correctly, the round ends with NO POINTS. The first player to reach 5 points wins the match.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    StackHeader("SECTION 3: MASTERY & THE BUFFER")
                    Text(
                        text = "The core of the simulation relies on THE BUFFER. When you execute a MOVE (Right Hand), that number is locked into your physical buffer and goes on cooldown for the next 2 rounds. You cannot select that number as a MOVE again until it cycles out of the buffer. The bot tracks your buffer and analyzes your historical patterns to optimize its defensive evasion and offensive predictions. Master the cooldown cycle to manipulate the bot's logic and break its defenses.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                TacticalButton(
                    text = "CLOSE ARCHIVE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsDialog(viewModel: UserPreferencesViewModel, onDismiss: () -> Unit) {
    val prefsState by viewModel.preferencesState.collectAsState()
    val initialUsername = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.username ?: ""
    var editUsername by remember { mutableStateOf(initialUsername) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BackgroundColor),
            border = BorderStroke(2.dp, Color.LightGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SYSTEM SETTINGS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AccentColor
                )
                Spacer(modifier = Modifier.height(32.dp))

                BlockyTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = "Edit Callsign",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = AccentColor
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                TacticalButton(
                    text = "SAVE CHANGES",
                    onClick = {
                        if (editUsername.isNotBlank()) {
                            viewModel.completeOnboarding(editUsername)
                            onDismiss()
                        }
                    },
                    isDisabled = !(editUsername.isNotBlank() && editUsername != initialUsername),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TacticalButton(
                    text = "TERMS OF SERVICE",
                    onClick = { /* TODO: Open TOS Dialog */ },
                    isDisabled = true,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TacticalButton(
                    text = "CLOSE",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}
