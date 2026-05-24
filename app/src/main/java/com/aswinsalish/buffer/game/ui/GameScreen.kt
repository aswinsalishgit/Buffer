package com.aswinsalish.buffer.game.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.core.components.HelpDialog
import com.aswinsalish.buffer.core.components.SettingsDialog
import com.aswinsalish.buffer.core.components.TacticalButton
import com.aswinsalish.buffer.core.components.BlockyTextField
import com.aswinsalish.buffer.core.components.StackHeader
import com.aswinsalish.buffer.core.components.glow
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.RepeatMode
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor
import com.aswinsalish.buffer.game.state.TurnPhase
import com.aswinsalish.buffer.game.viewmodel.GameViewModel

@Composable
fun GameScreen(
    difficulty: com.aswinsalish.buffer.game.state.BotDifficulty,
    onExitPlay: () -> Unit,
    gameViewModel: GameViewModel = viewModel(),
    prefsViewModel: UserPreferencesViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        gameViewModel.resetGame(difficulty)
    }
    
    val uiState by gameViewModel.uiState.collectAsState()
    val prefsState by prefsViewModel.preferencesState.collectAsState()
    val username = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.username ?: "YOU"
    val botInteractionsEnabled = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.botInteractionsEnabled ?: true
    
    var selectedRightHand by remember { mutableStateOf<Int?>(null) }
    var selectedLeftHand by remember { mutableStateOf<Int?>(null) }

    var showPauseDialog by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.width(48.dp)) // To keep text centered

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ROUND ${uiState.roundCount}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                Text(
                    text = "${username.uppercase()} ${uiState.playerScore} | BOT ${uiState.botScore}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
                
                Text(
                    text = "[ VS AI: ${difficulty.name} ]",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = { showPauseDialog = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Pause", tint = AccentColor)
            }
        }

        // Taunt Engine Display
        if (botInteractionsEnabled) {
            val displayedMessage = uiState.currentBotMessage
            if (displayedMessage != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val glowAlpha by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, delayMillis = 6000),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.aswinsalish.buffer.R.drawable.bot),
                        contentDescription = "Bot Avatar",
                        modifier = Modifier.size(64.dp).glow(color = AccentColor, alpha = glowAlpha)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    ) {
                        AnimatedContent(
                            targetState = displayedMessage,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                            }
                        ) { targetMessage ->
                            Text(
                                text = targetMessage,
                                modifier = Modifier.padding(12.dp),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(80.dp)) // Maintain layout stability
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
            if (showPauseDialog) {
                PauseDialog(
                    onDismiss = { showPauseDialog = false },
                    onExitPlay = onExitPlay,
                    prefsViewModel = prefsViewModel
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
fun PauseDialog(
    onDismiss: () -> Unit,
    onExitPlay: () -> Unit,
    prefsViewModel: UserPreferencesViewModel
) {
    var showHelp by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (showHelp) {
        HelpDialog(onDismiss = { showHelp = false })
    } else if (showSettings) {
        SettingsDialog(viewModel = prefsViewModel, onDismiss = { showSettings = false })
    } else {
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
                        text = "PAUSED",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AccentColor
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    TacticalButton(
                        text = "RESUME",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TacticalButton(
                        text = "EXIT PLAY",
                        onClick = onExitPlay,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TacticalButton(
                        text = "HELP",
                        onClick = { showHelp = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TacticalButton(
                        text = "SETTINGS",
                        onClick = { showSettings = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }
            }
        }
    }
}
