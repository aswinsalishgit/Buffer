package com.aswinsalish.buffer.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aswinsalish.buffer.R
import com.aswinsalish.buffer.core.components.TacticalButton
import com.aswinsalish.buffer.core.components.BlockyTextField
import com.aswinsalish.buffer.core.data.UserPreferencesViewModel
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor

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
            .background(BackgroundColor)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Image(
            painter = painterResource(id = R.drawable.ic_tactical_b),
            contentDescription = "Buffer Logo",
            modifier = Modifier.size(80.dp),
            colorFilter = ColorFilter.tint(AccentColor)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("BUFFER", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(48.dp))

        // Input
        BlockyTextField(
            value = username,
            onValueChange = { username = it },
            label = "Enter Callsign",
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User Icon",
                    tint = AccentColor
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Options
        TacticalButton(
            text = if (termsAccepted) "TERMS ACCEPTED" else "ACCEPT TERMS & POLICY",
            onClick = { termsAccepted = !termsAccepted },
            isActive = termsAccepted,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TacticalButton(
            text = "PLAY",
            onClick = {
                viewModel.completeOnboarding(username)
                onComplete()
            },
            isDisabled = !(username.isNotBlank() && termsAccepted),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}
