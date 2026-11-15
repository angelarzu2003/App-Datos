package mx.datoreal.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.datoreal.app.measurement.UsageAccess

/** Tarjeta minimalista: superficie + borde de 1 dp, sin sombras. */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

/** Cifra grande protagonista con una etiqueta debajo. */
@Composable
fun HeroNumber(value: String, caption: String, size: TextUnit = 44.sp) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = value,
            fontSize = size,
            lineHeight = size * 1.1f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ScreenTitle(text: String) {
    Text(
        text = text,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun Muted(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

/** Gráfica de línea simple de los últimos valores. */
@Composable
fun Sparkline(values: List<Double>, modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier.fillMaxWidth().height(48.dp)) {
        if (values.size < 2) return@Canvas
        val max = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
        val stepX = size.width / (values.size - 1)
        val path = Path()
        values.forEachIndexed { index, v ->
            val x = index * stepX
            val y = size.height - (v / max).toFloat() * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = color, style = Stroke(width = 3f, cap = StrokeCap.Round))
        drawLine(
            color = color.copy(alpha = 0.25f),
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = 1f
        )
    }
}

/** Aviso cuando falta el permiso de uso. */
@Composable
fun UsageAccessPrompt() {
    val context = LocalContext.current
    SectionCard {
        Text(
            "Falta un permiso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            "Para ver cuántos datos gasta cada app, Android pide activar “Acceso a datos de uso” " +
                "para DatoReal. Todo se queda en tu teléfono; la app no tiene permiso de internet.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = { UsageAccess.openSettings(context) }) {
            Text("Dar acceso")
        }
    }
}
