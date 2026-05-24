package com.aswinsalish.buffer.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.R
import com.aswinsalish.buffer.core.data.PreferencesState
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToGame: () -> Unit,
    viewModel: UserPreferencesViewModel = viewModel()
) {
    val preferencesState by viewModel.preferencesState.collectAsState()

    LaunchedEffect(preferencesState) {
        if (preferencesState is PreferencesState.Loaded) {
            val prefs = (preferencesState as PreferencesState.Loaded).prefs
            // Fake loading delay for aesthetic
            delay(1500) 
            if (prefs.username.isNullOrBlank() || !prefs.termsAccepted) {
                onNavigateToOnboarding()
            } else {
                onNavigateToGame()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_tactical_b),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(AccentColor)
            )
            Spacer(modifier = Modifier.height(32.dp))
            LinearProgressIndicator(
                color = AccentColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}
