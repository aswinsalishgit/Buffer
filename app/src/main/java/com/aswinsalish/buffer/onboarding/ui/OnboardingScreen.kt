package com.aswinsalish.buffer.onboarding.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.core.components.BlockyButton
import com.aswinsalish.buffer.core.components.BlockyTextField
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: UserPreferencesViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("INITIALIZE OPERATOR", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        BlockyTextField(
            value = username,
            onValueChange = { username = it },
            label = "Enter Callsign"
        )

        Spacer(modifier = Modifier.height(24.dp))

        BlockyButton(
            text = if (termsAccepted) "TERMS ACCEPTED" else "ACCEPT TERMS",
            onClick = { termsAccepted = !termsAccepted },
            isActive = termsAccepted,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(48.dp))

        BlockyButton(
            text = "ENTER SYSTEM",
            onClick = {
                viewModel.completeOnboarding(username)
                onComplete()
            },
            enabled = username.isNotBlank() && termsAccepted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
