package mx.datoreal.app.measurement

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager

enum class NetKind(val type: Int, val label: String) {
    MOBILE(ConnectivityManager.TYPE_MOBILE, "Datos móviles"),
    WIFI(ConnectivityManager.TYPE_WIFI, "Wi-Fi")
}

/** Bytes de un UID separados en "app en primer plano" y "resto". */
class UidBytes {
    var rxFg = 0L
    var txFg = 0L
    var rxBg = 0L
    var txBg = 0L

    val fg: Long get() = rxFg + txFg
    val bg: Long get() = rxBg + txBg
    val total: Long get() = fg + bg
}

/**
 * Lee los contadores que el sistema Android mantiene por app (NetworkStatsManager).
 * Necesita el permiso "Acceso a datos de uso".
 */
class NetworkUsageReader(context: Context) {

    private val manager: NetworkStatsManager =
        context.applicationContext.getSystemService(NetworkStatsManager::class.java)

    /** Consumo por UID entre [startMs] y [endMs]. */
    fun summaryByUid(kind: NetKind, startMs: Long, endMs: Long): Result<Map<Int, UidBytes>> =
        runCatching {
            val stats = manager.querySummary(kind.type, null, startMs, endMs)
            val acc = HashMap<Int, UidBytes>()
            try {
                val bucket = NetworkStats.Bucket()
                while (stats.hasNextBucket()) {
                    stats.getNextBucket(bucket)
                    val item = acc.getOrPut(bucket.uid) { UidBytes() }
                    if (bucket.state == NetworkStats.Bucket.STATE_FOREGROUND) {
                        item.rxFg += bucket.rxBytes
                        item.txFg += bucket.txBytes
                    } else {
                        item.rxBg += bucket.rxBytes
                        item.txBg += bucket.txBytes
                    }
                }
            } finally {
                stats.close()
            }
            acc
        }

    /** Total del dispositivo (rx, tx) entre [startMs] y [endMs]. */
    fun deviceTotal(kind: NetKind, startMs: Long, endMs: Long): Result<Pair<Long, Long>> =
        runCatching {
            val bucket = manager.querySummaryForDevice(kind.type, null, startMs, endMs)
            Pair(bucket.rxBytes, bucket.txBytes)
        }
}
