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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.datoreal.app.measurement.AppLabels
import mx.datoreal.app.measurement.NetKind
import mx.datoreal.app.measurement.NetworkUsageReader
import mx.datoreal.app.util.ByteFormat
import mx.datoreal.app.util.Period
import mx.datoreal.app.util.Periods

private data class LiveRow(val name: String, val deltaBytes: Long, val perSecond: Double)

@Composable
fun LiveScreen(granted: Boolean) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val labels = remember { AppLabels(context) }

    var rxPerSec by remember { mutableDoubleStateOf(0.0) }
    var txPerSec by remember { mutableDoubleStateOf(0.0) }
    var speeds by remember { mutableStateOf<List<Double>>(emptyList()) }
    var sessionBytes by remember { mutableLongStateOf(0L) }
    var supported by remember { mutableStateOf(true) }
    var rows by remember { mutableStateOf<List<LiveRow>>(emptyList()) }

    // Solo mide mientras la pantalla está visible (ahorra batería).
    LaunchedEffect(lifecycleOwner, granted) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

            // Velocidad total: TrafficStats cada 1 s
            launch {
                var lastRx = TrafficStats.getMobileRxBytes()
                var lastTx = TrafficStats.getMobileTxBytes()
                var lastT = SystemClock.elapsedRealtime()
                while (true) {
                    delay(1000)
                    val now = SystemClock.elapsedRealtime()
                    val rx = TrafficStats.getMobileRxBytes()
                    val tx = TrafficStats.getMobileTxBytes()
                    if (rx < 0 || tx < 0 || lastRx < 0 || lastTx < 0) {
                        supported = false
                        lastRx = rx
                        lastTx = tx
                        lastT = now
                        continue
                    }
                    supported = true
                    val dt = ((now - lastT) / 1000.0).coerceAtLeast(0.001)
                    val dRx = (rx - lastRx).coerceAtLeast(0L)
                    val dTx = (tx - lastTx).coerceAtLeast(0L)
                    rxPerSec = dRx / dt
                    txPerSec = dTx / dt
                    sessionBytes += dRx + dTx
                    speeds = (speeds + (rxPerSec + txPerSec)).takeLast(60)
                    lastRx = rx
                    lastTx = tx
                    lastT = now
                }
            }

            // Apps activas: NetworkStats cada 2 s (diferencia entre lecturas)
            if (granted) {
                launch {
                    val reader = NetworkUsageReader(context)
                    var prev: Map<Int, Long> = emptyMap()
                    var prevT = SystemClock.elapsedRealtime()
                    while (true) {
                        val result = withContext(Dispatchers.IO) {
                            reader.summaryByUid(
                                NetKind.MOBILE,
                                Periods.startOf(Period.TODAY),
                                System.currentTimeMillis()
                            )
                        }
                        val nowT = SystemClock.elapsedRealtime()
                        result.onSuccess { map ->
                            val current = map.mapValues { it.value.total }
                            val dt = ((nowT - prevT) / 1000.0).coerceAtLeast(0.001)
                            if (prev.isNotEmpty()) {
                                rows = current.mapNotNull { (uid, total) ->
                                    val delta = total - (prev[uid] ?: 0L)
                                    if (delta > 0) LiveRow(labels.label(uid).name, delta, delta / dt) else null
                                }.sortedByDescending { it.deltaBytes }.take(5)
                            }
                            prev = current
                            prevT = nowT
                        }
                        delay(2000)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("En vivo")

        if (!supported) {
            SectionCard {
                Text("Este teléfono no entrega contadores móviles en vivo.", fontWeight = FontWeight.Medium)
                Muted("Prueba con datos móviles encendidos y Wi-Fi apagado.")
            }
        }

        SectionCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HeroNumber("↓ " + ByteFormat.formatSpeed(rxPerSec), "Bajada", size = 24.sp)
                HeroNumber("↑ " + ByteFormat.formatSpeed(txPerSec), "Subida", size = 24.sp)
            }
            Sparkline(speeds)
            Muted("Datos móviles · últimos 60 s · esta sesión: ${ByteFormat.format(sessionBytes)}")
        }

        if (!granted) {
            UsageAccessPrompt()
        } else {
            Text("Ahora mismo", style = MaterialTheme.typography.titleMedium)
            SectionCard {
                if (rows.isEmpty()) {
                    Muted("Sin tráfico reciente por app, o el sistema aún no lo ha reflejado.")
                }
                rows.forEach { row ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(row.name, modifier = Modifier.weight(1f), maxLines = 1)
                        Text(
                            ByteFormat.formatSpeed(row.perSecond),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            Muted(
                "El detalle por app viene de NetworkStats y puede tardar unos segundos en reflejarse. " +
                    "Cuánto tarda es justo lo que mide la pestaña Prueba."
            )
        }
    }
}
