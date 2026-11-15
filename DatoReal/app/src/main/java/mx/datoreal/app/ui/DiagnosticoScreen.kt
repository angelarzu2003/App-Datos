package mx.datoreal.app.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.datoreal.app.measurement.DiagLine
import mx.datoreal.app.measurement.Diagnostics

@Composable
fun DiagnosticoScreen(granted: Boolean) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lines by remember { mutableStateOf<List<DiagLine>>(emptyList()) }
    var running by remember { mutableStateOf(false) }

    fun refresh() {
        scope.launch {
            running = true
            lines = withContext(Dispatchers.IO) { Diagnostics.collect(context, granted) }
            running = false
        }
    }

    LaunchedEffect(granted) { refresh() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("Estado")
        Muted("Fase 0: comprueba qué puede medir tu teléfono y cómo.")

        if (!granted) UsageAccessPrompt()

        SectionCard {
            if (lines.isEmpty()) {
                Text("Midiendo…", style = MaterialTheme.typography.bodyMedium)
            }
            lines.forEach { line ->
                val mark = when (line.ok) {
                    true -> "✓"
                    false -> "✗"
                    null -> "•"
                }
                val color = when (line.ok) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.error
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(mark, color = color)
                    Column {
                        Text(line.label, style = MaterialTheme.typography.labelLarge)
                        Muted(line.value)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { refresh() }, enabled = !running) { Text("Volver a medir") }
            OutlinedButton(onClick = { copyText(context, "Informe de estado", Diagnostics.asText(lines)) }) {
                Text("Copiar informe")
            }
        }
    }
}

internal fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
}
