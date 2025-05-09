package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.date.*
import at.orchaldir.gm.core.selector.time.date.resolveDay
import at.orchaldir.gm.core.selector.time.date.resolveMonth
import at.orchaldir.gm.core.selector.time.date.resolveWeek
import at.orchaldir.gm.core.selector.time.date.resolveYear
import at.orchaldir.gm.utils.NumberGenerator

class DateGenerator(
    private val numberGenerator: NumberGenerator,
    private val calendar: Calendar,
) {

    constructor(numberGenerator: NumberGenerator, state: State, id: CalendarId) :
            this(numberGenerator, state.getCalendarStorage().getOrThrow(id))

    fun generateMonthAndDay(date: Date): Day {
        val monthIndex = numberGenerator.getNumber() % calendar.months.getSize()
        val dayIndex = numberGenerator.getNumber() % calendar.months.getDaysPerMonth(monthIndex)

        val generatedDisplayDate = when (date) {
            is Day -> {
                val displayDay = calendar.resolveDay(date)
                DisplayDay(displayDay.month.year, monthIndex, dayIndex)
            }

            is DayRange -> error("Day Range is not supported!")

            is Week -> {
                val displayWeek = calendar.resolveWeek(date)
                DisplayDay(displayWeek.year, monthIndex, dayIndex)
            }

            is Month -> {
                val displayMonth = calendar.resolveMonth(date)
                DisplayDay(displayMonth.year, monthIndex, dayIndex)
            }

            is Year -> {
                val displayYear = calendar.resolveYear(date)
                DisplayDay(displayYear, monthIndex, dayIndex)
            }

            is Decade -> error("Cannot generate month & day for decade")
            is Century -> error("Cannot generate month & day for century")
        }

        return calendar.resolveDay(generatedDisplayDate)
    }

}


