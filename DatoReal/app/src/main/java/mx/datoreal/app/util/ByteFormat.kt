package mx.datoreal.app.util

import java.util.Locale
import kotlin.math.abs

/** Base de las unidades. Telcel define 1 GB = 1,024 MB (registros IFT), así que BINARY es el valor por defecto. */
enum class UnitBase(val step: Double) {
    BINARY(1024.0),
    DECIMAL(1000.0)
}

object ByteFormat {
    private val units = arrayOf("B", "KB", "MB", "GB", "TB")
    private val locale: Locale = Locale.forLanguageTag("es-MX")

    /** "3.42 GB", "412 MB", "1.5 KB". Nunca muestra "1024 KB": en ese caso sube a la siguiente unidad. */
    fun format(bytes: Long, base: UnitBase = UnitBase.BINARY): String {
        val negative = bytes < 0
        var value = abs(bytes.toDouble())
        var i = 0
        while (i < units.size - 1 && value >= base.step) {
            value /= base.step
            i++
        }
        if (i < units.size - 1 && roundedValue(value, i) >= base.step) {
            value /= base.step
            i++
        }
        val text = String.format(locale, "%.${decimals(value, i)}f", value)
        return (if (negative) "-" else "") + text + " " + units[i]
    }

    fun formatSpeed(bytesPerSecond: Double, base: UnitBase = UnitBase.BINARY): String =
        format(bytesPerSecond.toLong().coerceAtLeast(0L), base) + "/s"

    private fun decimals(value: Double, unitIndex: Int): Int = when {
        unitIndex == 0 -> 0
        unitIndex >= 3 -> 2
        value >= 100 -> 0
        else -> 1
    }

    private fun roundedValue(value: Double, unitIndex: Int): Double =
        String.format(Locale.US, "%.${decimals(value, unitIndex)}f", value).toDouble()
}
