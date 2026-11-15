package mx.datoreal.app.measurement

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.Build
import android.os.Process
import mx.datoreal.app.util.ByteFormat
import mx.datoreal.app.util.Period
import mx.datoreal.app.util.Periods

/** Una línea del informe de diagnóstico. [ok] null = informativo. */
data class DiagLine(val label: String, val value: String, val ok: Boolean? = null)

/** Fase 0: comprueba qué APIs de medición funcionan en ESTE teléfono. */
object Diagnostics {

    fun collect(context: Context, granted: Boolean): List<DiagLine> {
        val out = mutableListOf<DiagLine>()
        out += DiagLine("Teléfono", "${Build.MANUFACTURER} ${Build.MODEL}")
        out += DiagLine("Android", "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        out += DiagLine("Versión de software", Build.DISPLAY)
        out += DiagLine("Red activa", activeNetwork(context))
        out += DiagLine(
            "Acceso a datos de uso",
            if (granted) "Concedido" else "Falta concederlo",
            granted
        )

        // TrafficStats: totales de datos móviles desde el arranque
        val mobRx = TrafficStats.getMobileRxBytes()
        val mobTx = TrafficStats.getMobileTxBytes()
        val tsOk = mobRx >= 0 && mobTx >= 0
        out += DiagLine(
            "TrafficStats (móvil, desde el arranque)",
            if (tsOk) "↓ ${ByteFormat.format(mobRx)} · ↑ ${ByteFormat.format(mobTx)}" else "No disponible",
            tsOk
        )

        if (!granted) return out

        val reader = NetworkUsageReader(context)
        val now = System.currentTimeMillis()
        val start = Periods.startOf(Period.TODAY)

        // Móvil por app
        val t0 = System.nanoTime()
        val mobile = reader.summaryByUid(NetKind.MOBILE, start, now)
        val ms = (System.nanoTime() - t0) / 1_000_000
        mobile.onSuccess { map ->
            val total = map.values.sumOf { it.total }
            out += DiagLine(
                "NetworkStats móvil por app (hoy)",
                "${map.size} UIDs · total ${ByteFormat.format(total)} · consulta $ms ms",
                true
            )
            // ¿TrafficStats por UID funciona para otras apps?
            val top = map.entries.filter { it.key > 1000 && it.value.total > 0 }
                .sortedByDescending { it.value.total }.take(3)
            for ((uid, bytes) in top) {
                val rx = TrafficStats.getUidRxBytes(uid)
                val tx = TrafficStats.getUidTxBytes(uid)
                val usable = rx > 0 || tx > 0
                out += DiagLine(
                    "TrafficStats por UID $uid",
                    if (usable) {
                        "↓ ${ByteFormat.format(rx)} · ↑ ${ByteFormat.format(tx)} (NetworkStats hoy: ${ByteFormat.format(bytes.total)})"
                    } else {
                        "No disponible para apps ajenas (valor $rx / $tx)"
                    },
                    usable
                )
            }
        }.onFailure {
            out += DiagLine("NetworkStats móvil por app (hoy)", "ERROR: ${it.javaClass.simpleName}: ${it.message}", false)
        }

        // Móvil total del dispositivo
        reader.deviceTotal(NetKind.MOBILE, start, now).onSuccess { (rx, tx) ->
            out += DiagLine(
                "NetworkStats móvil total del dispositivo (hoy)",
                "↓ ${ByteFormat.format(rx)} · ↑ ${ByteFormat.format(tx)}",
                true
            )
        }.onFailure {
            out += DiagLine("NetworkStats móvil total del dispositivo", "ERROR: ${it.javaClass.simpleName}: ${it.message}", false)
        }

        // Wi-Fi por app
        reader.summaryByUid(NetKind.WIFI, start, now).onSuccess { map ->
            out += DiagLine("NetworkStats Wi-Fi por app (hoy)", "${map.size} UIDs · total ${ByteFormat.format(map.values.sumOf { it.total })}", true)
        }.onFailure {
            out += DiagLine("NetworkStats Wi-Fi por app (hoy)", "ERROR: ${it.javaClass.simpleName}: ${it.message}", false)
        }

        // Propia app
        val own = Process.myUid()
        out += DiagLine("UID de esta app", own.toString())
        return out
    }

    fun asText(lines: List<DiagLine>): String =
        lines.joinToString("\n") { l ->
            val mark = when (l.ok) {
                true -> "[OK]"
                false -> "[FALLA]"
                null -> "[--]"
            }
            "$mark ${l.label}: ${l.value}"
        }

    private fun activeNetwork(context: Context): String = runCatching {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        val caps = cm.getNetworkCapabilities(cm.activeNetwork)
        when {
            caps == null -> "Sin conexión"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Datos móviles"
            else -> "Otra"
        }
    }.getOrDefault("No se pudo leer")
}
