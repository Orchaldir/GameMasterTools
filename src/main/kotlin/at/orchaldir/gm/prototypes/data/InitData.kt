package at.orchaldir.gm.prototypes.data

import at.orchaldir.gm.core.model.ELEMENTS
import at.orchaldir.gm.core.model.State
import at.orchaldir.gm.core.model.createStorage
import at.orchaldir.gm.core.model.time.calendar.Calendar
import at.orchaldir.gm.core.model.time.calendar.CalendarId
import at.orchaldir.gm.core.model.time.calendar.ComplexMonths
import at.orchaldir.gm.core.model.time.calendar.MonthDefinition
import at.orchaldir.gm.core.model.time.calendar.SimpleMonths
import at.orchaldir.gm.core.model.time.calendar.WeekDay
import at.orchaldir.gm.core.model.time.calendar.Weekdays
import at.orchaldir.gm.core.model.util.name.Name
import at.orchaldir.gm.utils.Storage
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    logger.info { "Command line args: $args" }
    val path = args[0]
    logger.info { "Path: $path" }

    val state = State(
        ELEMENTS.associateWith { createStorage(it) },
        path,
    ).updateStorage(
        listOf(
            Storage(createDefaultCalendar())
        )
    )

    Files.createDirectories(Paths.get(path))

    state.save()
}

private fun createDefaultCalendar(): Calendar {
    val weekdays = Weekdays(
        listOf(
            WeekDay(Name.init("Monday")),
            WeekDay(Name.init("Tuesday")),
            WeekDay(Name.init("Wednesday")),
            WeekDay(Name.init("Thursday")),
            WeekDay(Name.init("Friday")),
            WeekDay(Name.init("Saturday")),
            WeekDay(Name.init("Sunday")),
        )
    )
    val months = ComplexMonths(
        listOf(
            MonthDefinition(31, "January"),
            MonthDefinition(28, "February"),
            MonthDefinition(31, "March"),
            MonthDefinition(30, "April"),
            MonthDefinition(31, "May"),
            MonthDefinition(30, "June"),
            MonthDefinition(31, "July"),
            MonthDefinition(31, "August"),
            MonthDefinition(30, "September"),
            MonthDefinition(31, "October"),
            MonthDefinition(30, "November"),
            MonthDefinition(31, "December"),
        )
    )
    return Calendar(
        CalendarId(0),
        Name.init("Default Calendar"),
        weekdays,
        months,
    )
}
