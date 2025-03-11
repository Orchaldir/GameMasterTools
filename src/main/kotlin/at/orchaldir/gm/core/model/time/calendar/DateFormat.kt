package at.orchaldir.gm.core.model.time.calendar

import kotlinx.serialization.Serializable

@Serializable
data class DateFormat(
    val order: DateOrder = DateOrder.DayMonthYear,
    val separator: Char = '.',
)
