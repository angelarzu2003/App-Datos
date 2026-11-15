package mx.datoreal.app.ui

import android.net.TrafficStats
import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.datoreal.app.measurement.AppLabels
import mx.datoreal.app.measurement.NetKind
import mx.datoreal.app.measurement.NetworkUsageReader
import mx.datoreal.app.util.ByteFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Lecturas de las dos fuentes en un instante. */
private class Totals(
    val atMs: Long,
    val tsRx: Long,
    val tsTx: Long,
    val nsRx: Long?,
    val nsTx: Long?,
    val perUid: Map<Int, Long>
) {
    val tsTotal: Long? get() = if (tsRx >= 0 && tsTx >= 0) tsRx + tsTx else null
    val nsTotal: Long? get() = if (nsRx != null && nsTx != null) nsRx + nsTx else null
}

private fun takeTotals(context: android.content.Context, withUid: Boolean): Totals {
    val now = System.currentTimeMillis()
    val start = now - 30L * 24 * 3600 * 1000
    val reader = NetworkUsageReader(context)
    val dev = reader.deviceTotal(NetKind.MOBILE, start, now).getOrNull()
    val per = if (withUid) {
        reader.summaryByUid(NetKind.MOBILE, start, now).getOrNull()
            ?.mapValues { it.value.total } ?: emptyMap()
    } else {
        emptyMap()
    }
    return Totals(
        atMs = now,
        tsRx = TrafficStats.getMobileRxBytes(),
        tsTx = TrafficStats.getMobileTxBytes(),
        nsRx = dev?.first,
        nsTx = dev?.second,
        perUid = per
    )
}

private fun delta(a: Long?, b: Long?): Long? = if (a != null && b != null) b - a else null

private fun clock(ms: Long): String = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(ms))

@Composable
fun PruebaScreen(granted: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val labels = remember { AppLabels(context) }

    // --- Prueba antes/después ---
    var start by remember { mutableStateOf<Totals?>(null) }
    var end by remember { mutableStateOf<Totals?>(null) }
    var busy by remember { mutableStateOf(false) }
    var ajustesText by remember { mutableStateOf("") }
    var telcelText by remember { mutableStateOf("") }
    var fileText by remember { mutableStateOf("") }
    var binary by remember { mutableStateOf(true) }

    // --- Prueba de frescura ---
    var freshJob by remember { mutableStateOf<Job?>(null) }
    var freshLog by remember { mutableStateOf("") }

    DisposableEffect(freshJob) {
        view.keepScreenOn = freshJob != null
        onDispose { view.keepScreenOn = false }
    }

    val mb = if (binary) 1_048_576.0 else 1_000_000.0
    fun parseMb(t: String): Double? = t.replace(',', '.').toDoubleOrNull()?.times(mb)

    val tsDelta = delta(start?.tsTotal, end?.tsTotal)
    val nsDelta = delta(start?.nsTotal, end?.nsTotal)
    val ajustes = parseMb(ajustesText)
    val telcel = parseMb(telcelText)
    val fileBytes = parseMb(fileText)

    fun dev(measured: Long?, reference: Double?): String =
        if (measured == null || reference == null || reference <= 0.0) "—"
        else String.format(Locale.US, "%+.1f %%", (measured - reference) / reference * 100.0)

    fun report(): String = buildString {
        appendLine("=== Prueba de precisión DatoReal ===")
        appendLine("Teléfono: ${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} · Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})")
        appendLine("Inicio: ${start?.let { clock(it.atMs) } ?: "—"}  Fin: ${end?.let { clock(it.atMs) } ?: "—"}")
        appendLine("TrafficStats Δ: ${tsDelta ?: "n/d"} bytes")
        appendLine("NetworkStats Δ: ${nsDelta ?: "n/d"} bytes")
        appendLine("Base de MB usada para tus cifras: ${if (binary) "1 MB = 1,048,576 B" else "1 MB = 1,000,000 B"}")
        appendLine("Ajustes de Android Δ: ${ajustes?.toLong() ?: "—"} bytes")
        appendLine("Mi Telcel Δ: ${telcel?.toLong() ?: "—"} bytes")
        appendLine("Tamaño del archivo K: ${fileBytes?.toLong() ?: "—"} bytes")
        appendLine("Desv. NetworkStats vs Ajustes: ${dev(nsDelta, ajustes)}")
        appendLine("Desv. NetworkStats vs Telcel: ${dev(nsDelta, telcel)}")
        appendLine("Desv. NetworkStats vs archivo: ${dev(nsDelta, fileBytes)}")
        appendLine("Desv. TrafficStats vs Ajustes: ${dev(tsDelta, ajustes)}")
        appendLine("Desv. TrafficStats vs Telcel: ${dev(tsDelta, telcel)}")
        appendLine("Desv. TrafficStats vs archivo: ${dev(tsDelta, fileBytes)}")
        val s = start
        val e = end
        if (s != null && e != null) {
            appendLine("Top apps (NetworkStats Δ):")
            e.perUid.entries
                .map { it.key to (it.value - (s.perUid[it.key] ?: 0L)) }
                .filter { it.second > 0 }
                .sortedByDescending { it.second }
                .take(5)
                .forEach { appendLine("  ${labels.label(it.first).name}: ${ByteFormat.format(it.second)}") }
        }
        if (freshLog.isNotBlank()) {
            appendLine("--- Frescura ---")
            appendLine(freshLog)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("Prueba")
        Muted("Protocolo de docs/07: compara lo que mide la app con Ajustes de Android y con Mi Telcel.")

        if (!granted) {
            UsageAccessPrompt()
            return@Column
        }

        // ---------- Antes / después ----------
        SectionCard {
            Text("1 · Antes y después", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Muted(
                "Apaga el Wi-Fi. Anota el consumo móvil en Ajustes de Android y el saldo en Mi Telcel. " +
                    "Toca INICIO, descarga un archivo de tamaño conocido, espera 2 minutos y toca FIN."
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !busy,
                    onClick = {
                        scope.launch {
                            busy = true
                            end = null
                            start = withContext(Dispatchers.IO) { takeTotals(context, true) }
                            busy = false
                        }
                    }
                ) { Text("Marcar INICIO") }
                Button(
                    enabled = !busy && start != null,
                    onClick = {
                        scope.launch {
                            busy = true
                            end = withContext(Dispatchers.IO) { takeTotals(context, true) }
                            busy = false
                        }
                    }
                ) { Text("Marcar FIN") }
            }
            start?.let { Muted("Inicio marcado a las ${clock(it.atMs)}") }
            end?.let { Muted("Fin marcado a las ${clock(it.atMs)}") }

            if (tsDelta != null || nsDelta != null) {
                Text(
                    "TrafficStats: ${tsDelta?.let { ByteFormat.format(it) } ?: "n/d"}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "NetworkStats: ${nsDelta?.let { ByteFormat.format(it) } ?: "n/d"}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        SectionCard {
            Text("2 · Tus referencias (en MB)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            NumberField("Ajustes de Android: lo que subió el consumo móvil", ajustesText) { ajustesText = it }
            NumberField("Mi Telcel: lo que bajó tu saldo de datos", telcelText) { telcelText = it }
            NumberField("Tamaño del archivo descargado", fileText) { fileText = it }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Switch(checked = binary, onCheckedChange = { binary = it })
                Column {
                    Text(if (binary) "1 MB = 1,048,576 bytes" else "1 MB = 1,000,000 bytes")
                    Muted("Elige la base que usa lo que estás copiando.")
                }
            }
            if (nsDelta != null || tsDelta != null) {
                Text("Desviación de NetworkStats", fontWeight = FontWeight.Medium)
                Muted("vs Ajustes ${dev(nsDelta, ajustes)} · vs Telcel ${dev(nsDelta, telcel)} · vs archivo ${dev(nsDelta, fileBytes)}")
                Text("Desviación de TrafficStats", fontWeight = FontWeight.Medium)
                Muted("vs Ajustes ${dev(tsDelta, ajustes)} · vs Telcel ${dev(tsDelta, telcel)} · vs archivo ${dev(tsDelta, fileBytes)}")
            }
        }

        // ---------- Frescura ----------
        SectionCard {
            Text("3 · ¿Qué tan rápido se refleja? (2 min)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Muted(
                "Con el Wi-Fi apagado, toca Empezar y descarga algo grande. Mide en cuántos segundos " +
                    "cada fuente refleja los primeros 100 KB. La pantalla se mantiene encendida."
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = freshJob == null,
                    onClick = {
                        freshLog = ""
                        freshJob = scope.launch {
                            val t0 = SystemClock.elapsedRealtime()
                            val base = withContext(Dispatchers.IO) { takeTotals(context, false) }
                            var tsAt: Long? = null
                            var nsAt: Long? = null
                            var last = ""
                            while (SystemClock.elapsedRealtime() - t0 < 120_000L) {
                                delay(1000)
                                val now = withContext(Dispatchers.IO) { takeTotals(context, false) }
                                val sec = (SystemClock.elapsedRealtime() - t0) / 1000
                                val tsD = delta(base.tsTotal, now.tsTotal) ?: 0L
                                val nsD = delta(base.nsTotal, now.nsTotal) ?: 0L
                                if (tsAt == null && tsD >= 100_000L) tsAt = sec
                                if (nsAt == null && nsD >= 100_000L) nsAt = sec
                                last = "t=+${sec}s · TrafficStats ${ByteFormat.format(tsD)} · NetworkStats ${ByteFormat.format(nsD)}"
                                freshLog = last
                            }
                            freshLog = "$last\nTrafficStats llegó a 100 KB en: ${tsAt?.let { "${it}s" } ?: "nunca"}\n" +
                                "NetworkStats llegó a 100 KB en: ${nsAt?.let { "${it}s" } ?: "nunca"}"
                            freshJob = null
                        }
                    }
                ) { Text("Empezar") }
                OutlinedButton(
                    enabled = freshJob != null,
                    onClick = {
                        freshJob?.cancel()
                        freshJob = null
                    }
                ) { Text("Detener") }
            }
            if (freshLog.isNotBlank()) Muted(freshLog)
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { copyText(context, "Informe de prueba", report()) }
        ) { Text("Copiar informe completo") }
    }
}

@Composable
private fun NumberField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}
