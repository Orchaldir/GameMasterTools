package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.time.Date
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.core.model.time.DisplayDay
import at.orchaldir.gm.core.model.time.Year
import at.orchaldir.gm.utils.NumberGenerator

class DateGenerator(
    private val numberGenerator: NumberGenerator,
    private val calendar: Calendar,
) {

    constructor(numberGenerator: NumberGenerator, state: State, id: CalendarId) :
            this(numberGenerator, state.getCalendarStorage().getOrThrow(id))

    fun generateMonthAndDay(date: Date): Day {
        val monthIndex = numberGenerator.getNumber() % calendar.months.size
        val dayIndex = numberGenerator.getNumber() % calendar.months[monthIndex].days

        val generatedDisplayDate = when (date) {
            is Day -> {
                val displayDay = calendar.resolve(date)
                displayDay.copy(monthIndex = monthIndex, dayIndex = dayIndex)
            }

            is Year -> {
                val displayYear = calendar.resolve(date)
                DisplayDay(displayYear, monthIndex, dayIndex)
            }
        }

        return calendar.resolve(generatedDisplayDate)
    }

}


