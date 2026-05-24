package com.aswinsalish.buffer.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor
import com.aswinsalish.buffer.core.audio.SoundManager
import com.aswinsalish.buffer.core.audio.SoundType

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
                        text = "If your READ matches the opponent's MOVE exactly, you score a point. If both players successfully read each other, it's a DRAW and both players score a point. If neither reads the opponent correctly, the round ends with NO POINTS. The first player to reach 3 points wins the match.",
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    clickSound = SoundType.SWIPE
                )
            }
        }
    }
}

@Composable
fun SettingsDialog(viewModel: UserPreferencesViewModel, onDismiss: () -> Unit) {
    val prefsState by viewModel.preferencesState.collectAsState()
    val initialUsername = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.username ?: ""
    val defaultDifficulty = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.defaultDifficulty ?: com.aswinsalish.buffer.game.state.BotDifficulty.MEDIUM
    val botInteractionsEnabled = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.botInteractionsEnabled ?: true
    val sfxEnabled = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.sfxEnabled ?: true
    val musicEnabled = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.musicEnabled ?: true
    val musicVolume = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.musicVolume ?: 0.5f
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
                
                StackHeader("DEFAULT BOT DIFFICULTY")
                DifficultySelector(
                    selectedDifficulty = defaultDifficulty,
                    onDifficultySelected = { viewModel.saveDefaultDifficulty(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                StackHeader("BOT COMMUNICATIONS")
                TacticalButton(
                    text = if (botInteractionsEnabled) "ENABLED" else "DISABLED",
                    onClick = { 
                        val newState = !botInteractionsEnabled
                        SoundManager.playSound(if (newState) SoundType.TOGGLE_ON else SoundType.TOGGLE_OFF)
                        viewModel.toggleBotInteractions(newState) 
                    },
                    isActive = botInteractionsEnabled,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    clickSound = null
                )

                Spacer(modifier = Modifier.height(32.dp))

                StackHeader("AUDIO SETTINGS")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TacticalButton(
                        text = if (sfxEnabled) "SFX ON" else "SFX OFF",
                        onClick = { 
                            val newState = !sfxEnabled
                            SoundManager.playSound(if (newState) SoundType.TOGGLE_ON else SoundType.TOGGLE_OFF)
                            viewModel.toggleSfx(newState) 
                        },
                        isActive = sfxEnabled,
                        modifier = Modifier.weight(1f).height(48.dp),
                        clickSound = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    TacticalButton(
                        text = if (musicEnabled) "MUSIC ON" else "MUSIC OFF",
                        onClick = { 
                            val newState = !musicEnabled
                            SoundManager.playSound(if (newState) SoundType.TOGGLE_ON else SoundType.TOGGLE_OFF)
                            viewModel.toggleMusic(newState) 
                        },
                        isActive = musicEnabled,
                        modifier = Modifier.weight(1f).height(48.dp),
                        clickSound = null
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "MUSIC VOLUME",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Start)
                )
                Slider(
                    value = musicVolume,
                    onValueChange = { viewModel.setMusicVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = AccentColor,
                        activeTrackColor = AccentColor,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    clickSound = SoundType.SWIPE
                )
            }
        }
    }
}
