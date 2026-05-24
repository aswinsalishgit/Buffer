package com.aswinsalish.buffer.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aswinsalish.buffer.core.theme.AccentColor
import com.aswinsalish.buffer.core.theme.BackgroundColor
import com.aswinsalish.buffer.core.audio.SoundManager
import com.aswinsalish.buffer.core.audio.SoundType

fun Modifier.glow(
    color: Color,
    alpha: Float = 0.5f,
    blurRadius: Dp = 12.dp,
) = this.drawBehind {
    val paint = Paint().apply {
        val frameworkPaint = asFrameworkPaint()
        frameworkPaint.color = android.graphics.Color.TRANSPARENT
        frameworkPaint.setShadowLayer(
            blurRadius.toPx(),
            0f,
            0f,
            android.graphics.Color.argb(
                (alpha * 255).toInt(),
                (color.red * 255).toInt(),
                (color.green * 255).toInt(),
                (color.blue * 255).toInt()
            )
        )
    }
    drawIntoCanvas { canvas ->
        canvas.drawRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            paint = paint
        )
    }
}

@Composable
fun TacticalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    isDisabled: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    clickSound: SoundType? = SoundType.CLICK
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isGlowing = isActive || isPressed

    val baseColor = Color(0xFF2A2E38)
    
    val containerColor = if (isDisabled) Color.DarkGray.copy(alpha = 0.3f) else baseColor
    val contentColor = if (isDisabled) Color.Gray.copy(alpha = 0.5f) else if (isActive) Color.White else Color.LightGray
    
    val borderColor = if (isActive && !isDisabled) AccentColor else if (isDisabled) Color.DarkGray else Color.DarkGray.copy(alpha = 0.5f)
    val borderWidth = if (isActive && !isDisabled) 2.dp else 1.dp

    val glowModifier = if (isGlowing && !isDisabled) {
        modifier.glow(color = AccentColor)
    } else modifier

    Button(
        onClick = {
            clickSound?.let { SoundManager.playSound(it) }
            onClick()
        },
        modifier = glowModifier,
        enabled = !isDisabled,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        border = BorderStroke(borderWidth, borderColor),
        interactionSource = interactionSource,
        contentPadding = contentPadding
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BlockyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = leadingIcon,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentColor,
            unfocusedBorderColor = Color.DarkGray,
            focusedContainerColor = BackgroundColor,
            unfocusedContainerColor = BackgroundColor
        )
    )
}

@Composable
fun StackHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DifficultySelector(
    selectedDifficulty: com.aswinsalish.buffer.game.state.BotDifficulty,
    onDifficultySelected: (com.aswinsalish.buffer.game.state.BotDifficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
    ) {
        com.aswinsalish.buffer.game.state.BotDifficulty.values().forEach { difficulty ->
            TacticalButton(
                text = difficulty.name,
                onClick = { onDifficultySelected(difficulty) },
                isActive = selectedDifficulty == difficulty,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            )
        }
    }
}
