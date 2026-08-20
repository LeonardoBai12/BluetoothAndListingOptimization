package io.lb.bleandlistingopt.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF3B6BFF)
private val Secondary = Color(0xFF5E5E72)

private val LightColors = lightColorScheme(primary = Primary, secondary = Secondary)
private val DarkColors = darkColorScheme(primary = Primary, secondary = Secondary)

@Composable
fun BleLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
