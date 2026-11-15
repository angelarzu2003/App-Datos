package mx.datoreal.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.datoreal.app.measurement.AppLabels
import mx.datoreal.app.measurement.NetKind
import mx.datoreal.app.measurement.NetworkUsageReader
import mx.datoreal.app.measurement.UidBytes
import mx.datoreal.app.util.ByteFormat
import mx.datoreal.app.util.Period
import mx.datoreal.app.util.Periods

private data class AppRow(val uid: Int, val name: String, val bytes: UidBytes)

private sealed interface AppsState {
    data object Loading : AppsState
    data class Ready(val rows: List<AppRow>, val total: Long) : AppsState
    data class Error(val message: String) : AppsState
}

@Composable
fun AppsScreen(granted: Boolean) {
    val context = LocalContext.current
    val labels = remember { AppLabels(context) }
    var period by remember { mutableStateOf(Period.TODAY) }
    var net by remember { mutableStateOf(NetKind.MOBILE) }
    var state by remember { mutableStateOf<AppsState>(AppsState.Loading) }

    LaunchedEffect(granted, period, net) {
        if (!granted) return@LaunchedEffect
        state = AppsState.Loading
        state = withContext(Dispatchers.IO) {
            val reader = NetworkUsageReader(context)
            val result = reader.summaryByUid(net, Periods.startOf(period), System.currentTimeMillis())
            result.fold(
                onSuccess = { map ->
                    val rows = map.entries
                        .filter { it.value.total > 0 }
                        .map { AppRow(it.key, labels.label(it.key).name, it.value) }
                        .sortedByDescending { it.bytes.total }
                    AppsState.Ready(rows, rows.sumOf { it.bytes.total })
                },
                onFailure = { AppsState.Error("${it.javaClass.simpleName}: ${it.message}") }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(Modifier.padding(top = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ScreenTitle("Apps")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Period.entries.forEach { p ->
                    FilterChip(selected = period == p, onClick = { period = p }, label = { Text(p.label) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NetKind.entries.forEach { n ->
                    FilterChip(selected = net == n, onClick = { net = n }, label = { Text(n.label) })
                }
            }
        }

        if (!granted) {
            UsageAccessPrompt()
            return@Column
        }

        when (val s = state) {
            AppsState.Loading -> Muted("Leyendo…")
            is AppsState.Error -> SectionCard {
                Text("No se pudo leer el consumo", fontWeight = FontWeight.Medium)
                Muted(s.message)
            }
            is AppsState.Ready -> {
                HeroNumber(ByteFormat.format(s.total), "${net.label} · ${period.label}")
                if (s.rows.isEmpty()) {
                    Muted("Aún no hay consumo en este periodo.")
                }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(s.rows, key = { it.uid }) { row ->
                        AppUsageRow(row, s.total)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUsageRow(row: AppRow, total: Long) {
    val percent = if (total > 0) row.bytes.total * 100.0 / total else 0.0
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                row.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                ByteFormat.format(row.bytes.total),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Muted(
            "Primer plano ${ByteFormat.format(row.bytes.fg)} · Segundo plano ${ByteFormat.format(row.bytes.bg)} · " +
                String.format(java.util.Locale.US, "%.1f %%", percent)
        )
    }
}
