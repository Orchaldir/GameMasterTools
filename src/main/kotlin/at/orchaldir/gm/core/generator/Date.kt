package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.selector.time.resolve
import at.orchaldir.gm.core.model.time.*
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
                val displayDay = calendar.resolve(date)
                displayDay.copy(monthIndex = monthIndex, dayIndex = dayIndex)
            }

            is Year -> {
                val displayYear = calendar.resolve(date)
                DisplayDay(displayYear, monthIndex, dayIndex)
            }

            is Decade -> error("Cannot generate month & day for decade")
            is Century -> error("Cannot generate month & day for century")
        }

        return calendar.resolve(generatedDisplayDate)
    }

}


