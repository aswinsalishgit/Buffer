package com.aswinsalish.buffer.menu.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.R
import com.aswinsalish.buffer.core.components.HelpDialog
import com.aswinsalish.buffer.core.components.SettingsDialog
import com.aswinsalish.buffer.core.components.TacticalButton
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor

@Composable
fun MainMenuScreen(
    onPlayClick: (com.aswinsalish.buffer.game.state.BotDifficulty) -> Unit,
    prefsViewModel: UserPreferencesViewModel = viewModel()
) {
    val prefsState by prefsViewModel.preferencesState.collectAsState()
    val defaultDifficulty = (prefsState as? com.aswinsalish.buffer.core.data.PreferencesState.Loaded)?.prefs?.defaultDifficulty ?: com.aswinsalish.buffer.game.state.BotDifficulty.MEDIUM
    var selectedDifficulty by remember(defaultDifficulty) { mutableStateOf(defaultDifficulty) }
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
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { showHelpDialog = true }) {
                Icon(Icons.Default.Info, contentDescription = "Help", tint = AccentColor)
            }
            IconButton(onClick = { showSettingsDialog = true }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AccentColor)
            }
        }

        // Center Logo
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tactical_b),
                    contentDescription = "Buffer Logo",
                    modifier = Modifier.size(120.dp),
                    colorFilter = ColorFilter.tint(AccentColor)
                )
                
                Spacer(modifier = Modifier.height(64.dp))
                
                com.aswinsalish.buffer.core.components.DifficultySelector(
                    selectedDifficulty = selectedDifficulty,
                    onDifficultySelected = { selectedDifficulty = it },
                    modifier = Modifier.padding(horizontal = 32.dp).padding(bottom = 32.dp)
                )

                TacticalButton(
                    text = "PLAY",
                    onClick = { onPlayClick(selectedDifficulty) },
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(64.dp)
                )
            }
        }
    }

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
