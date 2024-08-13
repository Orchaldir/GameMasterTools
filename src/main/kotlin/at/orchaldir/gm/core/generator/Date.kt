package at.orchaldir.gm.core.generator

import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.calendar.Calendar
import at.orchaldir.gm.core.model.calendar.CalendarId
import at.orchaldir.gm.core.model.time.Day
import at.orchaldir.gm.utils.NumberGenerator

class DateGenerator(
    private val numberGenerator: NumberGenerator,
    private val calendar: Calendar,
) {

    constructor(numberGenerator: NumberGenerator, state: State, id: CalendarId) :
            this(numberGenerator, state.calendars.getOrThrow(id))

    fun generateMonthAndDay(day: Day): Day {
        val displayDay = calendar.resolve(day)
        val monthIndex = numberGenerator.getNumber().toInt() % calendar.months.size
        val dayIndex = numberGenerator.getNumber().toInt() % calendar.months[monthIndex].days
        val generatedDisplayDate = displayDay.copy(monthIndex = monthIndex, dayIndex = dayIndex)
        return calendar.resolve(generatedDisplayDate)
    }

}


