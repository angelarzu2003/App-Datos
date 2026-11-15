package mx.datoreal.app.util

import java.time.LocalDate
import java.time.ZoneId

enum class Period(val label: String, val daysBack: Long) {
    TODAY("Hoy", 0),
    WEEK("7 días", 6),
    MONTH("30 días", 29)
}

object Periods {
    /** Milisegundos del inicio del día (hora local) de hace [daysBack] días. */
    fun startOfDay(daysBack: Long = 0): Long =
        LocalDate.now().minusDays(daysBack)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    fun startOf(period: Period): Long = startOfDay(period.daysBack)
}
