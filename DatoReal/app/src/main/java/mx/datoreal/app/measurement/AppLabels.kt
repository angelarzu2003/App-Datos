package mx.datoreal.app.measurement

import android.app.usage.NetworkStats
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process

data class AppLabel(val name: String, val packageName: String?)

/** Convierte un UID de Android en un nombre legible para mostrar. */
class AppLabels(context: Context) {
    private val pm: PackageManager = context.applicationContext.packageManager
    private val cache = HashMap<Int, AppLabel>()

    fun label(uid: Int): AppLabel = cache.getOrPut(uid) { resolve(uid) }

    private fun resolve(uid: Int): AppLabel {
        when (uid) {
            NetworkStats.Bucket.UID_REMOVED -> return AppLabel("Apps desinstaladas", null)
            NetworkStats.Bucket.UID_TETHERING -> return AppLabel("Compartir internet", null)
            Process.SYSTEM_UID -> return AppLabel("Sistema Android", null)
            0 -> return AppLabel("Sistema (root)", null)
        }
        val packages = pm.getPackagesForUid(uid)
        if (packages.isNullOrEmpty()) return AppLabel("UID $uid", null)
        val first = packages[0]
        val name = try {
            @Suppress("DEPRECATION")
            pm.getApplicationLabel(pm.getApplicationInfo(first, 0)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            first
        }
        val suffix = if (packages.size > 1) " +${packages.size - 1}" else ""
        return AppLabel(name + suffix, first)
    }
}
