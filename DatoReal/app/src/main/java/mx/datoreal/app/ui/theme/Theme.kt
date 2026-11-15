package mx.datoreal.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de docs/04-diseno-ux-ui.md §2.1 (minimalista, un solo acento).
private val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCFBF1),
    onPrimaryContainer = Color(0xFF134E4A),
    background = Color(0xFFFAFAF9),
    onBackground = Color(0xFF1C1917),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1917),
    surfaceVariant = Color(0xFFF5F5F4),
    onSurfaceVariant = Color(0xFF78716C),
    outline = Color(0xFFE7E5E4),
    outlineVariant = Color(0xFFE7E5E4),
    error = Color(0xFFB91C1C),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF2DD4BF),
    onPrimary = Color(0xFF0C0A09),
    primaryContainer = Color(0xFF134E4A),
    onPrimaryContainer = Color(0xFFCCFBF1),
    background = Color(0xFF0C0A09),
    onBackground = Color(0xFFFAFAF9),
    surface = Color(0xFF1C1917),
    onSurface = Color(0xFFFAFAF9),
    surfaceVariant = Color(0xFF292524),
    onSurfaceVariant = Color(0xFFA8A29E),
    outline = Color(0xFF292524),
    outlineVariant = Color(0xFF292524),
    error = Color(0xFFF87171),
    onError = Color(0xFF0C0A09)
)

/** Color de "atención" (ámbar) que Material no trae. */
object StatusColors {
    val warnLight = Color(0xFFB45309)
    val warnDark = Color(0xFFFBBF24)
}

@Composable
fun DatoRealTheme(content: @Composable () -> Unit) {
    // Sigue el tema del sistema (decisión del usuario).
    val colors = if (isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, content = content)
}
